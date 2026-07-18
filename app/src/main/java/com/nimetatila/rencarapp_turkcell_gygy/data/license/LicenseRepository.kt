package com.nimetatila.rencarapp_turkcell_gygy.data.license

import retrofit2.Response

interface LicenseRepository {

    suspend fun uploadLicense(
        frontBytes: ByteArray,
        frontFileName: String,
        frontMimeType: String,
        backBytes: ByteArray,
        backFileName: String,
        backMimeType: String,
        selfieBytes: ByteArray,
        selfieFileName: String,
        selfieMimeType: String
    ): Response<LicenseResponse>

    suspend fun getLicenseStatus(): Response<LicenseStatusResponse>
}
