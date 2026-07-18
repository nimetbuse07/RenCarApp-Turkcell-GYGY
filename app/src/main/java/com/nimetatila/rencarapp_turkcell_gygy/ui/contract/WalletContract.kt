package com.nimetatila.rencarapp_turkcell_gygy.ui.contract

import androidx.compose.runtime.Stable
import com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletTransactionDto
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardResponseDto

@Stable
data class WalletState(
    val balance: Double = 0.0,
    val transactions: List<WalletTransactionDto> = emptyList(),
    val cards: List<CardResponseDto> = emptyList(),
    val isWalletLoading: Boolean = false,
    val isCardsLoading: Boolean = false,
    val isTopupLoading: Boolean = false,
    val isCardAdding: Boolean = false,
    val walletError: String? = null,
    val cardsError: String? = null,
    val topupError: String? = null,
    val cardAddingError: String? = null,
    val topupSuccess: Boolean = false,
    val cardAddingSuccess: Boolean = false
)

sealed interface WalletIntent {
    object LoadWallet : WalletIntent
    object LoadCards : WalletIntent
    data class TopupWallet(val amount: Double) : WalletIntent
    data class AddCard(val brand: String, val last4: String, val expMonth: Int, val expYear: Int) : WalletIntent
    data class SetDefaultCard(val id: String) : WalletIntent
    object ClearErrors : WalletIntent
}

sealed interface WalletEffect {
    data class ShowMessage(val message: String) : WalletEffect
}
