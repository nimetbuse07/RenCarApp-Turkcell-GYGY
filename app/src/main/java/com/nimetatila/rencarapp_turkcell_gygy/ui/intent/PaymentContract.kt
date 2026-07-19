package com.nimetatila.rencarapp_turkcell_gygy.ui.intent

import androidx.compose.runtime.Stable
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.PayRentalResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardResponseDto

@Stable
data class PaymentState(
    val rentalId: String = "",
    val rentalDetails: RentalResponseDto? = null,
    val cards: List<CardResponseDto> = emptyList(),
    val selectedCard: CardResponseDto? = null,
    val discountCode: String = "",
    val isRentalLoading: Boolean = false,
    val isCardsLoading: Boolean = false,
    val isPaying: Boolean = false,
    val rentalError: String? = null,
    val cardsError: String? = null,
    val paymentError: String? = null,
    val paymentSuccess: Boolean = false,
    val paymentReceipt: PayRentalResponseDto? = null,
    val selectedPaymentMethod: String = "CARD", // "CARD", "WALLET" or "IYZICO"
    val walletBalance: Double = 0.0,
    val isWalletLoading: Boolean = false,
    val walletError: String? = null,
    val showWebView: Boolean = false,
    val webViewUrl: String? = null,
    val iyzicoToken: String? = null
)

sealed interface PaymentIntent {
    data class LoadDetails(val rentalId: String) : PaymentIntent
    object LoadCards : PaymentIntent
    object LoadWallet : PaymentIntent
    data class SelectCard(val card: CardResponseDto) : PaymentIntent
    data class SelectPaymentMethod(val method: String) : PaymentIntent
    data class ChangeDiscountCode(val code: String) : PaymentIntent
    object PayRental : PaymentIntent
    object ClearErrors : PaymentIntent
    data class CompleteIyzicoPayment(val token: String) : PaymentIntent
    object CancelIyzicoPayment : PaymentIntent
}

sealed interface PaymentEffect {
    object NavigateToDashboard : PaymentEffect
}
