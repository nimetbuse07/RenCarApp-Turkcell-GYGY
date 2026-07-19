package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.contract.LicenseEffect
import com.nimetatila.rencarapp_turkcell_gygy.contract.LicenseIntent
import com.nimetatila.rencarapp_turkcell_gygy.contract.LicenseState
import com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val licenseRepository: LicenseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(LicenseState())
    val state: StateFlow<LicenseState> = _state.asStateFlow()

    private val _effect = Channel<LicenseEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LicenseIntent) {
        when (intent) {
            is LicenseIntent.GetStatus -> {
                getLicenseStatus()
            }
            is LicenseIntent.FrontImageChanged -> {
                val bitmap = decodeUriToBitmap(intent.uri)
                _state.value = _state.value.copy(
                    frontImageUri = intent.uri,
                    frontBitmap = bitmap
                )
            }
            is LicenseIntent.BackImageChanged -> {
                val bitmap = decodeUriToBitmap(intent.uri)
                _state.value = _state.value.copy(
                    backImageUri = intent.uri,
                    backBitmap = bitmap
                )
            }
            is LicenseIntent.SelfieImageChanged -> {
                val bitmap = decodeUriToBitmap(intent.uri)
                _state.value = _state.value.copy(
                    selfieImageUri = intent.uri,
                    selfieBitmap = bitmap
                )
            }
            is LicenseIntent.UploadLicense -> {
                uploadLicense()
            }
            is LicenseIntent.ClearError -> {
                _state.value = _state.value.copy(
                    statusError = null,
                    uploadError = null
                )
            }
        }
    }

    private fun getLicenseStatus() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isStatusLoading = true, statusError = null)
            try {
                val response = licenseRepository.getLicenseStatus()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isStatusLoading = false,
                        statusResponse = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Ehliyet durumu alınamadı"
                    _state.value = _state.value.copy(
                        isStatusLoading = false,
                        statusError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isStatusLoading = false,
                    statusError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun uploadLicense() {
        val frontUri = _state.value.frontImageUri
        val backUri = _state.value.backImageUri
        val selfieUri = _state.value.selfieImageUri

        if (frontUri == null || backUri == null || selfieUri == null) {
            _state.value = _state.value.copy(uploadError = "Lütfen tüm fotoğrafları (ön, arka, selfie) seçin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isUploading = true, uploadError = null, uploadSuccess = false)
            try {
                val frontBytes = getBytesFromUri(frontUri)
                val backBytes = getBytesFromUri(backUri)
                val selfieBytes = getBytesFromUri(selfieUri)

                if (frontBytes == null || backBytes == null || selfieBytes == null) {
                    _state.value = _state.value.copy(isUploading = false, uploadError = "Dosyalar okunurken bir hata oluştu")
                    return@launch
                }

                val frontMimeType = context.contentResolver.getType(frontUri) ?: "image/jpeg"
                val backMimeType = context.contentResolver.getType(backUri) ?: "image/jpeg"
                val selfieMimeType = context.contentResolver.getType(selfieUri) ?: "image/jpeg"

                val response = licenseRepository.uploadLicense(
                    frontBytes = frontBytes,
                    frontFileName = "front.jpg",
                    frontMimeType = frontMimeType,
                    backBytes = backBytes,
                    backFileName = "back.jpg",
                    backMimeType = backMimeType,
                    selfieBytes = selfieBytes,
                    selfieFileName = "selfie.jpg",
                    selfieMimeType = selfieMimeType
                )

                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isUploading = false,
                        uploadSuccess = true
                    )
                    _effect.send(LicenseEffect.NavigateToDashboard)
                    getLicenseStatus() // Refresh status
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Ehliyet yüklenemedi"
                    _state.value = _state.value.copy(
                        isUploading = false,
                        uploadError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isUploading = false,
                    uploadError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun decodeUriToBitmap(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getBytesFromUri(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

                val maxDimension = 1200
                val width = originalBitmap.width
                val height = originalBitmap.height
                val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                    val ratio = width.toFloat() / height.toFloat()
                    val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
                    val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
                    Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                } else {
                    originalBitmap
                }

                val outputStream = java.io.ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

                if (scaledBitmap != originalBitmap) {
                    scaledBitmap.recycle()
                }
                originalBitmap.recycle()

                outputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
