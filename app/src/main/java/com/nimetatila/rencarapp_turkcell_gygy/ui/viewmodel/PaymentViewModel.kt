package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.PayRentalDto
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.PaymentEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.PaymentIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.PaymentState
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
    private val cardRepository: CardRepository
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
            is PaymentIntent.SelectCard -> {
                _state.value = _state.value.copy(
                    selectedCard = intent.card
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
                    paymentError = null
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

    private fun payRental() {
        val currentState = _state.value
        val rentalId = currentState.rentalId
        val cardId = currentState.selectedCard?.id

        if (cardId == null) {
            _state.value = currentState.copy(paymentError = "Lütfen geçerli bir ödeme yöntemi seçin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isPaying = true, paymentError = null, paymentSuccess = false)
            try {
                val requestDto = PayRentalDto(
                    method = "CARD",
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

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = org.json.JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
