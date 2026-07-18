package com.nimetatila.rencarapp_turkcell_gygy.data.reservation

import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationApi: ReservationApi
) : ReservationRepository {
    override suspend fun createReservation(vehicleId: String): Result<ReservationResponseDto> {
        return try {
            val response = reservationApi.createReservation(CreateReservationDto(vehicleId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Rezervasyon oluşturulamadı"
                Result.failure(Exception(parseError(errorMsg)))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = org.json.JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
