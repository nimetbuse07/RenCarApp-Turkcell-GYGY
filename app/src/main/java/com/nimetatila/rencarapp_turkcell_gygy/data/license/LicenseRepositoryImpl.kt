package com.nimetatila.rencarapp_turkcell_gygy.data.license

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseRepositoryImpl @Inject constructor(
    private val licenseApi: LicenseApi
) : LicenseRepository {

    override suspend fun uploadLicense(
        frontBytes: ByteArray,
        frontFileName: String,
        frontMimeType: String,
        backBytes: ByteArray,
        backFileName: String,
        backMimeType: String,
        selfieBytes: ByteArray,
        selfieFileName: String,
        selfieMimeType: String
    ): Response<LicenseResponse> {
        val frontRequestBody = frontBytes.toRequestBody(frontMimeType.toMediaTypeOrNull(), 0, frontBytes.size)
        val frontPart = MultipartBody.Part.createFormData("front", frontFileName, frontRequestBody)

        val backRequestBody = backBytes.toRequestBody(backMimeType.toMediaTypeOrNull(), 0, backBytes.size)
        val backPart = MultipartBody.Part.createFormData("back", backFileName, backRequestBody)

        val selfieRequestBody = selfieBytes.toRequestBody(selfieMimeType.toMediaTypeOrNull(), 0, selfieBytes.size)
        val selfiePart = MultipartBody.Part.createFormData("selfie", selfieFileName, selfieRequestBody)

        return licenseApi.uploadLicense(frontPart, backPart, selfiePart)
    }

    override suspend fun getLicenseStatus(): Response<LicenseStatusResponse> {
        return licenseApi.getLicenseStatus()
    }
}
