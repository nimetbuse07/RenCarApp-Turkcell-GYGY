package com.nimetatila.rencarapp_turkcell_gygy.data.rental

import retrofit2.Response

interface RentalRepository {
    suspend fun createRental(vehicleId: String, plan: String, endDate: String? = null): Response<RentalResponseDto>
    suspend fun getActiveRental(): Response<ActiveRentalResponseDto>
    suspend fun uploadRentalPhoto(id: String, side: String, photoBytes: ByteArray, mimeType: String): Response<RentalPhotosStateDto>
    suspend fun startRental(id: String): Response<RentalResponseDto>
    suspend fun finishRental(id: String): Response<FinishRentalResponseDto>
    suspend fun getRentalDetails(id: String): Response<RentalResponseDto>
    suspend fun payRental(id: String, request: PayRentalDto): Response<PayRentalResponseDto>
}
