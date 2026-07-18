package com.nimetatila.rencarapp_turkcell_gygy.data.card

import kotlinx.serialization.Serializable

@Serializable
data class CardResponseDto(
    val id: String,
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
    val isDefault: Boolean,
    val createdAt: String
)

@Serializable
data class CreateCardDto(
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int
)
