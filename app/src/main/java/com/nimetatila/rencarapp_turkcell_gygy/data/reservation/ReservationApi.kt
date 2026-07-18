package com.nimetatila.rencarapp_turkcell_gygy.data.reservation


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReservationApi {
    @POST("reservations")
    suspend fun createReservation(
        @Body request: CreateReservationDto
    ): Response<ReservationResponseDto>
}
