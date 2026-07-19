package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.wallet.TopupDto
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CreateCardDto
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.WalletEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.WalletIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.WalletState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    private val _effect = Channel<WalletEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: WalletIntent) {
        when (intent) {
            is WalletIntent.LoadWallet -> {
                loadWallet()
            }
            is WalletIntent.LoadCards -> {
                loadCards()
            }
            is WalletIntent.TopupWallet -> {
                topupWallet(intent.amount)
            }
            is WalletIntent.AddCard -> {
                addCard(intent.brand, intent.last4, intent.expMonth, intent.expYear)
            }
            is WalletIntent.SetDefaultCard -> {
                setDefaultCard(intent.id)
            }
            is WalletIntent.ClearErrors -> {
                _state.value = _state.value.copy(
                    walletError = null,
                    cardsError = null,
                    topupError = null,
                    cardAddingError = null,
                    topupSuccess = false,
                    cardAddingSuccess = false
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
                        balance = walletResponse.balance,
                        transactions = walletResponse.transactions
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

    private fun loadCards() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCardsLoading = true, cardsError = null)
            try {
                val response = cardRepository.getCards()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isCardsLoading = false,
                        cards = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kartlar yüklenemedi"
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

    private fun topupWallet(amount: Double) {
        if (_state.value.cards.isEmpty()) {
            _state.value = _state.value.copy(
                topupError = "Bakiye yüklemek için lütfen önce bir kart ekleyin"
            )
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isTopupLoading = true, topupError = null, topupSuccess = false)
            try {
                val response = walletRepository.topupWallet(TopupDto(amount))
                if (response.isSuccessful && response.body() != null) {
                    val walletResponse = response.body()!!
                    _state.value = _state.value.copy(
                        isTopupLoading = false,
                        balance = walletResponse.balance,
                        transactions = walletResponse.transactions,
                        topupSuccess = true
                    )
                    _effect.send(WalletEffect.ShowMessage("₺${String.format("%.2f", amount)} başarıyla yüklendi"))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Bakiye yükleme başarısız"
                    _state.value = _state.value.copy(
                        isTopupLoading = false,
                        topupError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isTopupLoading = false,
                    topupError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun addCard(brand: String, last4: String, expMonth: Int, expYear: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCardAdding = true, cardAddingError = null, cardAddingSuccess = false)
            try {
                val response = cardRepository.createCard(CreateCardDto(brand, last4, expMonth, expYear))
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isCardAdding = false,
                        cardAddingSuccess = true
                    )
                    _effect.send(WalletEffect.ShowMessage("Yeni kart başarıyla eklendi"))
                    loadCards() // Refresh cards
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kart eklenemedi"
                    _state.value = _state.value.copy(
                        isCardAdding = false,
                        cardAddingError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCardAdding = false,
                    cardAddingError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun setDefaultCard(cardId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCardsLoading = true, cardsError = null)
            try {
                val response = cardRepository.setDefaultCard(cardId)
                if (response.isSuccessful && response.body() != null) {
                    _effect.send(WalletEffect.ShowMessage("Varsayılan kart güncellendi"))
                    loadCards() // Reload cards list to reflect updated default card
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Varsayılan kart güncellenemedi"
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

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = org.json.JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
