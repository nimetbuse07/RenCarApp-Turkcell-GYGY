package com.nimetatila.rencarapp_turkcell_gygy.ui.contract

import android.net.Uri
import androidx.compose.runtime.Stable
import com.nimetatila.rencarapp_turkcell_gygy.data.reservation.ReservationResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleResponseDto

@Stable
data class ReservationState(
    val vehicleId: String = "",
    val vehicle: VehicleResponseDto? = null,
    val isLoadingVehicle: Boolean = false,

    // Photo Upload Step
    val photos: Map<String, Uri?> = mapOf(
        "ön" to null,
        "arka" to null,
        "sol" to null,
        "sağ" to null
    ),

    // Approval Step
    val selectedPlan: String = "PER_MINUTE", // "PER_MINUTE", "HOURLY", "DAILY"
    val isAgreedToTerms: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val successReservation: ReservationResponseDto? = null
)

sealed interface ReservationIntent {
    data class LoadVehicle(val vehicleId: String) : ReservationIntent
    data class PhotoSelected(val direction: String, val uri: Uri) : ReservationIntent
    data class PlanSelected(val plan: String) : ReservationIntent
    data class AgreedToTermsChanged(val isChecked: Boolean) : ReservationIntent
    object SubmitReservation : ReservationIntent
    object ClearError : ReservationIntent
}

sealed interface ReservationEffect {
    object NavigateToSuccess : ReservationEffect
    data class ShowError(val message: String) : ReservationEffect
}
