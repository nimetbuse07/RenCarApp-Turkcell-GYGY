package com.nimetatila.rencarapp_turkcell_gygy.data.reservation

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationDto(
    val vehicleId: String
)

@Serializable
data class ReservationVehicleSummaryDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val pricePerMinute: Double
)

@Serializable
data class ReservationResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: ReservationVehicleSummaryDto,
    val status: String,
    val expiresAt: String,
    val remainingSeconds: Int,
    val createdAt: String
)
