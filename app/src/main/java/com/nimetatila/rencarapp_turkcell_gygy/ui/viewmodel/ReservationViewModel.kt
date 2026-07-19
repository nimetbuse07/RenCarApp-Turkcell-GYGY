package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.reservation.ReservationRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.ReservationEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.ReservationIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.ReservationState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository,
    private val rentalRepository: RentalRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ReservationState())
    val state: StateFlow<ReservationState> = _state.asStateFlow()

    private val _effect = Channel<ReservationEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ReservationIntent) {
        when (intent) {
            is ReservationIntent.LoadVehicle -> {
                loadVehicle(intent.vehicleId)
            }
            is ReservationIntent.PhotoSelected -> {
                val updatedPhotos = _state.value.photos.toMutableMap()
                updatedPhotos[intent.direction] = intent.uri
                _state.value = _state.value.copy(photos = updatedPhotos)
            }
            is ReservationIntent.PlanSelected -> {
                _state.value = _state.value.copy(selectedPlan = intent.plan)
            }
            is ReservationIntent.AgreedToTermsChanged -> {
                _state.value = _state.value.copy(isAgreedToTerms = intent.isChecked)
            }
            is ReservationIntent.SubmitReservation -> {
                submitReservation()
            }
            is ReservationIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun loadVehicle(vehicleId: String) {
        if (_state.value.vehicleId == vehicleId && _state.value.vehicle != null) return

        viewModelScope.launch {
            _state.value = _state.value.copy(vehicleId = vehicleId, isLoadingVehicle = true, error = null)
            val result = vehicleRepository.getVehicle(vehicleId)
            result.onSuccess { vehicle ->
                _state.value = _state.value.copy(vehicle = vehicle, isLoadingVehicle = false)
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    error = exception.message ?: "Araç bilgileri yüklenemedi",
                    isLoadingVehicle = false
                )
            }
        }
    }

    private fun submitReservation() {
        val vehicleId = _state.value.vehicleId
        if (vehicleId.isBlank()) {
            _state.value = _state.value.copy(error = "Geçersiz araç bilgisi")
            return
        }
        if (!_state.value.isAgreedToTerms) {
            _state.value = _state.value.copy(error = "Lütfen kullanım şartlarını kabul edin")
            return
        }

        val plan = _state.value.selectedPlan

        viewModelScope.launch {
            _state.value = _state.value.copy(isSubmitting = true, error = null)

            // 1. Create Reservation
            val resResult = reservationRepository.createReservation(vehicleId)
            resResult.onSuccess { reservation ->
                _state.value = _state.value.copy(successReservation = reservation)

                // 2. Create Rental
                try {
                    val rentalResponse = rentalRepository.createRental(vehicleId, plan, null)
                    if (rentalResponse.isSuccessful && rentalResponse.body() != null) {
                        val rental = rentalResponse.body()!!

                        // If plan is PER_MINUTE or HOURLY, upload photos and start rental
                        if (plan == "PER_MINUTE" || plan == "HOURLY") {
                            val uploadSuccess = uploadAllPhotos(rental.id)
                            if (uploadSuccess) {
                                val startResponse = rentalRepository.startRental(rental.id)
                                if (startResponse.isSuccessful) {
                                    _state.value = _state.value.copy(isSubmitting = false)
                                    _effect.send(ReservationEffect.NavigateToSuccess)
                                } else {
                                    val err = startResponse.errorBody()?.string() ?: "Kiralama başlatılamadı"
                                    showSubmitError(err)
                                }
                            } else {
                                showSubmitError("Fotoğraflar yüklenirken bir hata oluştu")
                            }
                        } else {
                            // DAILY starts immediately
                            _state.value = _state.value.copy(isSubmitting = false)
                            _effect.send(ReservationEffect.NavigateToSuccess)
                        }
                    } else {
                        val err = rentalResponse.errorBody()?.string() ?: "Kiralama oluşturulamadı"
                        showSubmitError(err)
                    }
                } catch (e: Exception) {
                    showSubmitError(e.message ?: "Kiralama işlemi sırasında bağlantı hatası oluştu")
                }

            }.onFailure { exception ->
                val errMsg = exception.message ?: "Rezervasyon tamamlanamadı"
                showSubmitError(errMsg)
            }
        }
    }

    private suspend fun uploadAllPhotos(rentalId: String): Boolean {
        val photosMap = _state.value.photos
        val sideMapping = mapOf(
            "ön" to "FRONT",
            "arka" to "BACK",
            "sol" to "LEFT",
            "sağ" to "RIGHT"
        )

        for ((localKey, side) in sideMapping) {
            val uri = photosMap[localKey] ?: return false
            val photoBytes = getBytesFromUri(uri) ?: return false
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

            try {
                val uploadResponse = rentalRepository.uploadRentalPhoto(rentalId, side, photoBytes, mimeType)
                if (!uploadResponse.isSuccessful) {
                    return false
                }
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }

    private fun showSubmitError(message: String) {
        val parsedMsg = try {
            val jsonObject = org.json.JSONObject(message)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            message
        }
        _state.value = _state.value.copy(error = parsedMsg, isSubmitting = false)
        viewModelScope.launch {
            _effect.send(ReservationEffect.ShowError(parsedMsg))
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
                val outputStream = ByteArrayOutputStream()
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
}
