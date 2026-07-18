package com.nimetatila.rencarapp_turkcell_gygy.data.rental

import kotlinx.serialization.Serializable

@Serializable
data class RentalVehicleSummaryDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String
)

@Serializable
data class RentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String,
    val endedAt: String? = null,
    val endDate: String? = null,
    val totalPrice: Double? = null,
    val startFee: Double,
    val serviceFee: Double? = null,
    val distanceKm: Double,
    val durationMinutes: Double,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String? = null,
    val discountAmount: Double,
    val createdAt: String,
    val startDate: String? = null
)

@Serializable
data class PayRentalDto(
    val method: String,
    val cardId: String? = null,
    val discountCode: String? = null
)

@Serializable
data class PaidCardSummaryDto(
    val brand: String,
    val last4: String
)

@Serializable
data class PayRentalResponseDto(
    val rentalId: String,
    val paymentStatus: String,
    val method: String,
    val totalPrice: Double,
    val discountAmount: Double,
    val paidAmount: Double,
    val walletBalance: Double? = null,
    val card: PaidCardSummaryDto? = null
)

@Serializable
data class CreateRentalDto(
    val vehicleId: String,
    val plan: String,
    val endDate: String? = null
)

@Serializable
data class RentalPhotoDto(
    val side: String,
    val imageUrl: String,
    val createdAt: String
)

@Serializable
data class RentalPhotosStateDto(
    val rentalId: String,
    val photos: List<RentalPhotoDto>,
    val uploadedCount: Int,
    val remainingSides: List<String>
)

@Serializable
data class ActiveRentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String,
    val endedAt: String? = null,
    val endDate: String? = null,
    val totalPrice: Double? = null,
    val startFee: Double,
    val serviceFee: Double? = null,
    val distanceKm: Double,
    val durationMinutes: Double,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String? = null,
    val discountAmount: Double,
    val createdAt: String,
    val elapsedSeconds: Double,
    val currentCost: Double
)

@Serializable
data class FinishRentalResponseDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummaryDto,
    val plan: String,
    val startedAt: String,
    val endedAt: String? = null,
    val endDate: String? = null,
    val totalPrice: Double? = null,
    val startFee: Double,
    val serviceFee: Double? = null,
    val distanceKm: Double,
    val durationMinutes: Double,
    val status: String,
    val paymentStatus: String,
    val paymentMethod: String? = null,
    val discountAmount: Double,
    val createdAt: String,
    val usageFee: Double,
    val elapsedSeconds: Double
)
