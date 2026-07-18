package com.nimetatila.rencarapp_turkcell_gygy.data.reservation


interface ReservationRepository {
    suspend fun createReservation(vehicleId: String): Result<ReservationResponseDto>
}
