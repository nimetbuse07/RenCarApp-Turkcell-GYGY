package com.nimetatila.rencarapp_turkcell_gygy.ui.contract

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
    val paymentReceipt: PayRentalResponseDto? = null
)

sealed interface PaymentIntent {
    data class LoadDetails(val rentalId: String) : PaymentIntent
    object LoadCards : PaymentIntent
    data class SelectCard(val card: CardResponseDto) : PaymentIntent
    data class ChangeDiscountCode(val code: String) : PaymentIntent
    object PayRental : PaymentIntent
    object ClearErrors : PaymentIntent
}

sealed interface PaymentEffect {
    object NavigateToDashboard : PaymentEffect
}
