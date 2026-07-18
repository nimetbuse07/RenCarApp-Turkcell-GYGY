package com.nimetatila.rencarapp_turkcell_gygy.data.wallet

import kotlinx.serialization.Serializable

@Serializable
data class WalletTransactionDto(
    val id: String,
    val type: String,
    val amount: Double,
    val rentalId: String? = null,
    val description: String,
    val createdAt: String
)

@Serializable
data class WalletResponseDto(
    val id: String,
    val balance: Double,
    val transactions: List<WalletTransactionDto>
)

@Serializable
data class TopupDto(
    val amount: Double
)
