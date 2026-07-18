package com.nimetatila.rencarapp_turkcell_gygy.data.rental

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface RentalApi {

    @POST("rentals")
    suspend fun createRental(
        @Body request: CreateRentalDto
    ): Response<RentalResponseDto>

    @GET("rentals/active")
    suspend fun getActiveRental(): Response<ActiveRentalResponseDto>

    @Multipart
    @POST("rentals/{id}/photos")
    suspend fun uploadRentalPhoto(
        @Path("id") id: String,
        @Part side: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Response<RentalPhotosStateDto>

    @POST("rentals/{id}/start")
    suspend fun startRental(
        @Path("id") id: String
    ): Response<RentalResponseDto>

    @POST("rentals/{id}/finish")
    suspend fun finishRental(
        @Path("id") id: String
    ): Response<FinishRentalResponseDto>

    @GET("rentals/{id}")
    suspend fun getRentalDetails(
        @Path("id") id: String
    ): Response<RentalResponseDto>

    @POST("rentals/{id}/pay")
    suspend fun payRental(
        @Path("id") id: String,
        @Body request: PayRentalDto
    ): Response<PayRentalResponseDto>
}
