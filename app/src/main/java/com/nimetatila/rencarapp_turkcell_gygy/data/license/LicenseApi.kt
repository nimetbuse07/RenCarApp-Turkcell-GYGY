package com.nimetatila.rencarapp_turkcell_gygy.data.license


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LicenseApi {

    @Multipart
    @POST("license/upload")
    suspend fun uploadLicense(
        @Part front: MultipartBody.Part,
        @Part back: MultipartBody.Part,
        @Part selfie: MultipartBody.Part
    ): Response<LicenseResponse>

    @GET("license/status")
    suspend fun getLicenseStatus(): Response<LicenseStatusResponse>
}
