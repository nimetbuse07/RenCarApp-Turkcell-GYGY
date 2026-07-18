package com.nimetatila.rencarapp_turkcell_gygy.contract


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Stable
import com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseStatusResponse

@Stable
data class LicenseState(
    val statusResponse: LicenseStatusResponse? = null,
    val isStatusLoading: Boolean = true,
    val statusError: String? = null,
    val frontImageUri: Uri? = null,
    val backImageUri: Uri? = null,
    val selfieImageUri: Uri? = null,
    val frontBitmap: Bitmap? = null,
    val backBitmap: Bitmap? = null,
    val selfieBitmap: Bitmap? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null,
    val uploadSuccess: Boolean = false
)

sealed interface LicenseIntent {
    object GetStatus : LicenseIntent
    data class FrontImageChanged(val uri: Uri) : LicenseIntent
    data class BackImageChanged(val uri: Uri) : LicenseIntent
    data class SelfieImageChanged(val uri: Uri) : LicenseIntent
    object UploadLicense : LicenseIntent
    object ClearError : LicenseIntent
}

sealed interface LicenseEffect {
    object NavigateToSelfie : LicenseEffect
    object NavigateToDashboard : LicenseEffect
}
