package com.nimetatila.rencarapp_turkcell_gygy.data.rental

import retrofit2.Response
import javax.inject.Inject

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class RentalRepositoryImpl @Inject constructor(
    private val rentalApi: RentalApi
) : RentalRepository {

    override suspend fun createRental(
        vehicleId: String,
        plan: String,
        endDate: String?
    ): Response<RentalResponseDto> {
        return rentalApi.createRental(CreateRentalDto(vehicleId, plan, endDate))
    }

    override suspend fun getActiveRental(): Response<ActiveRentalResponseDto> {
        return rentalApi.getActiveRental()
    }

    override suspend fun uploadRentalPhoto(
        id: String,
        side: String,
        photoBytes: ByteArray,
        mimeType: String
    ): Response<RentalPhotosStateDto> {
        val sideRequestBody = side.toRequestBody("text/plain".toMediaTypeOrNull())
        val sidePart = MultipartBody.Part.createFormData("side", null, sideRequestBody)

        val fileRequestBody = photoBytes.toRequestBody(mimeType.toMediaTypeOrNull(), 0, photoBytes.size)
        val filePart = MultipartBody.Part.createFormData("file", "photo.jpg", fileRequestBody)

        return rentalApi.uploadRentalPhoto(id, sidePart, filePart)
    }

    override suspend fun startRental(id: String): Response<RentalResponseDto> {
        return rentalApi.startRental(id)
    }

    override suspend fun finishRental(id: String): Response<FinishRentalResponseDto> {
        return rentalApi.finishRental(id)
    }

    override suspend fun getRentalDetails(id: String): Response<RentalResponseDto> {
        return rentalApi.getRentalDetails(id)
    }

    override suspend fun payRental(id: String, request: PayRentalDto): Response<PayRentalResponseDto> {
        return rentalApi.payRental(id, request)
    }
}
