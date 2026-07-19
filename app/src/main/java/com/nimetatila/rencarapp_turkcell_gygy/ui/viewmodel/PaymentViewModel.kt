package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.PayRentalDto
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.InitializeCheckoutFormDto
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.PaymentEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.PaymentIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.PaymentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val cardRepository: CardRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    private val _effect = Channel<PaymentEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: PaymentIntent) {
        when (intent) {
            is PaymentIntent.LoadDetails -> {
                loadRentalDetails(intent.rentalId)
            }
            is PaymentIntent.LoadCards -> {
                loadCards()
            }
            is PaymentIntent.LoadWallet -> {
                loadWallet()
            }
            is PaymentIntent.SelectCard -> {
                _state.value = _state.value.copy(
                    selectedCard = intent.card
                )
            }
            is PaymentIntent.SelectPaymentMethod -> {
                _state.value = _state.value.copy(
                    selectedPaymentMethod = intent.method
                )
            }
            is PaymentIntent.ChangeDiscountCode -> {
                _state.value = _state.value.copy(
                    discountCode = intent.code
                )
            }
            is PaymentIntent.PayRental -> {
                payRental()
            }
            is PaymentIntent.ClearErrors -> {
                _state.value = _state.value.copy(
                    rentalError = null,
                    cardsError = null,
                    paymentError = null,
                    walletError = null
                )
            }
            is PaymentIntent.CompleteIyzicoPayment -> {
                completeIyzicoPayment(intent.token)
            }
            is PaymentIntent.CancelIyzicoPayment -> {
                _state.value = _state.value.copy(
                    showWebView = false,
                    webViewUrl = null,
                    iyzicoToken = null
                )
            }
        }
    }

    private fun loadRentalDetails(rentalId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRentalLoading = true, rentalId = rentalId, rentalError = null)
            try {
                val response = rentalRepository.getRentalDetails(rentalId)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isRentalLoading = false,
                        rentalDetails = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kiralama detayları yüklenemedi"
                    _state.value = _state.value.copy(
                        isRentalLoading = false,
                        rentalError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isRentalLoading = false,
                    rentalError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCardsLoading = true, cardsError = null)
            try {
                val response = cardRepository.getCards()
                if (response.isSuccessful && response.body() != null) {
                    val cardsList = response.body()!!
                    val defaultCard = cardsList.firstOrNull { it.isDefault } ?: cardsList.firstOrNull()
                    _state.value = _state.value.copy(
                        isCardsLoading = false,
                        cards = cardsList,
                        selectedCard = defaultCard
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kart bilgileri yüklenemedi"
                    _state.value = _state.value.copy(
                        isCardsLoading = false,
                        cardsError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCardsLoading = false,
                    cardsError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isWalletLoading = true, walletError = null)
            try {
                val response = walletRepository.getWallet()
                if (response.isSuccessful && response.body() != null) {
                    val walletResponse = response.body()!!
                    _state.value = _state.value.copy(
                        isWalletLoading = false,
                        walletBalance = walletResponse.balance
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Cüzdan bilgileri yüklenemedi"
                    _state.value = _state.value.copy(
                        isWalletLoading = false,
                        walletError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isWalletLoading = false,
                    walletError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun payRental() {
        val currentState = _state.value
        val rentalId = currentState.rentalId
        val paymentMethod = currentState.selectedPaymentMethod
        val cardId = if (paymentMethod == "CARD") currentState.selectedCard?.id else null

        if (paymentMethod == "CARD" && cardId == null) {
            _state.value = currentState.copy(paymentError = "Lütfen geçerli bir ödeme yöntemi seçin")
            return
        }

        if (paymentMethod == "WALLET") {
            val rentalDetails = currentState.rentalDetails
            if (rentalDetails != null) {
                val totalPriceVal = rentalDetails.totalPrice ?: (rentalDetails.startFee + (rentalDetails.serviceFee ?: 0.0))
                val finalPriceVal = maxOf(0.0, totalPriceVal - (if (rentalDetails.discountAmount > 0.0) rentalDetails.discountAmount else if (currentState.discountCode.isNotBlank()) 20.0 else 0.0))

                if (currentState.walletBalance < finalPriceVal) {
                    _state.value = currentState.copy(
                        paymentError = "Cüzdan bakiyeniz yetersizdir. Lütfen bakiye yükleyin."
                    )
                    return
                }
            }
        }

        if (paymentMethod == "IYZICO") {
            val rentalDetails = currentState.rentalDetails
            if (rentalDetails != null) {
                val totalPriceVal = rentalDetails.totalPrice ?: (rentalDetails.startFee + (rentalDetails.serviceFee ?: 0.0))
                // Note: Discount code is not allowed for Iyzico payment as per OpenAPI spec
                initializeIyzico(rentalId, totalPriceVal)
            } else {
                _state.value = currentState.copy(paymentError = "Kiralama detayları eksik")
            }
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isPaying = true, paymentError = null, paymentSuccess = false)
            try {
                val requestDto = PayRentalDto(
                    method = paymentMethod,
                    cardId = cardId,
                    discountCode = currentState.discountCode.takeIf { it.isNotBlank() }
                )
                val response = rentalRepository.payRental(rentalId, requestDto)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isPaying = false,
                        paymentSuccess = true,
                        paymentReceipt = response.body()!!
                    )
                    _effect.send(PaymentEffect.NavigateToDashboard)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Ödeme gerçekleştirilemedi"
                    _state.value = _state.value.copy(
                        isPaying = false,
                        paymentError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isPaying = false,
                    paymentError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun initializeIyzico(rentalId: String, price: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isPaying = true, paymentError = null, paymentSuccess = false)
            try {
                val request = InitializeCheckoutFormDto(
                    price = price,
                    description = "RenCar yolculuk ödemesi",
                    basketId = "rental-$rentalId",
                    enabledInstallments = listOf(1)
                )
                val response = rentalRepository.initializeIyzico(request)
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    _state.value = _state.value.copy(
                        isPaying = false,
                        showWebView = true,
                        webViewUrl = responseBody.paymentPageUrl,
                        iyzicoToken = responseBody.token
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "İyzico ödeme başlatılamadı"
                    _state.value = _state.value.copy(
                        isPaying = false,
                        paymentError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isPaying = false,
                    paymentError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun completeIyzicoPayment(token: String) {
        val currentState = _state.value
        val rentalId = currentState.rentalId

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isPaying = true,
                paymentError = null,
                showWebView = false,
                webViewUrl = null
            )
            try {
                val resultResponse = rentalRepository.getIyzicoResult(token)
                if (resultResponse.isSuccessful && resultResponse.body() != null) {
                    val paymentResult = resultResponse.body()!!
                    if (paymentResult.paymentStatus == "SUCCESS") {
                        val payRequest = PayRentalDto(
                            method = "IYZICO",
                            iyzicoPaymentId = paymentResult.paymentId
                        )
                        val payResponse = rentalRepository.payRental(rentalId, payRequest)
                        if (payResponse.isSuccessful && payResponse.body() != null) {
                            _state.value = _state.value.copy(
                                isPaying = false,
                                paymentSuccess = true,
                                paymentReceipt = payResponse.body()!!
                            )
                            _effect.send(PaymentEffect.NavigateToDashboard)
                        } else {
                            val errorMsg = payResponse.errorBody()?.string() ?: "Ödeme kaydı oluşturulamadı"
                            _state.value = _state.value.copy(
                                isPaying = false,
                                paymentError = parseError(errorMsg)
                            )
                        }
                    } else {
                        _state.value = _state.value.copy(
                            isPaying = false,
                            paymentError = "Ödeme işlemi başarısız: ${paymentResult.paymentStatus}"
                        )
                    }
                } else {
                    val errorMsg = resultResponse.errorBody()?.string() ?: "Ödeme durumu doğrulanamadı"
                    _state.value = _state.value.copy(
                        isPaying = false,
                        paymentError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isPaying = false,
                    paymentError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = org.json.JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
