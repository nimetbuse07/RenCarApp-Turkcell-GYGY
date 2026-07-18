package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.ActiveRentalResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RideLocationClient
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ActiveRentalIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ActiveRentalState
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ActiveRentalEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveRentalViewModel @Inject constructor(
    private val rentalRepository: RentalRepository,
    private val rideLocationClient: RideLocationClient
) : ViewModel() {

    private val _state = MutableStateFlow(ActiveRentalState())
    val state: StateFlow<ActiveRentalState> = _state.asStateFlow()

    private val _effect = Channel<ActiveRentalEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var pollingJob: Job? = null
    private var locationJob: Job? = null

    fun onIntent(intent: ActiveRentalIntent) {
        when (intent) {
            is ActiveRentalIntent.LoadActiveRental -> {
                loadActiveRental(intent.rentalId)
            }
            is ActiveRentalIntent.EndRental -> {
                endRental()
            }
            is ActiveRentalIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    fun checkActiveRental() {
        viewModelScope.launch {
            try {
                val response = rentalRepository.getActiveRental()
                if (response.isSuccessful && response.body() != null) {
                    val rental = response.body()!!
                    _state.value = _state.value.copy(
                        rentalId = rental.id,
                        activeRental = rental
                    )
                    startUpdates(rental.id)
                }
            } catch (e: Exception) {
                // Ignore or log error
            }
        }
    }

    private fun loadActiveRental(rentalId: String) {
        _state.value = _state.value.copy(rentalId = rentalId, isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = rentalRepository.getActiveRental()
                if (response.isSuccessful && response.body() != null) {
                    val rental = response.body()!!
                    _state.value = _state.value.copy(
                        activeRental = rental,
                        isLoading = false
                    )
                    startUpdates(rentalId)
                } else {
                    _state.value = _state.value.copy(
                        error = "Aktif kiralama detayları yüklenemedi",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Bağlantı hatası oluştu",
                    isLoading = false
                )
            }
        }
    }

    private fun startUpdates(rentalId: String) {
        pollingJob?.cancel()
        locationJob?.cancel()

        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(5000)
                try {
                    val response = rentalRepository.getActiveRental()
                    if (response.isSuccessful && response.body() != null) {
                        _state.value = _state.value.copy(
                            activeRental = response.body()
                        )
                    }
                } catch (e: Exception) {
                    // Ignore transient errors
                }
            }
        }

        locationJob = viewModelScope.launch {
            rideLocationClient.vehiclePositionStream().collect { point ->
                _state.value = _state.value.copy(carLocation = point)
            }
        }
    }

    private fun endRental() {
        val rentalId = _state.value.rentalId
        if (rentalId.isBlank()) return

        _state.value = _state.value.copy(isEndingRental = true, error = null)
        viewModelScope.launch {
            try {
                val response = rentalRepository.finishRental(rentalId)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isEndingRental = false,
                        endedRentalSummary = response.body()
                    )
                    pollingJob?.cancel()
                    locationJob?.cancel()
                    _effect.send(ActiveRentalEffect.NavigateToPaymentSummary(rentalId))
                } else {
                    val errMsg = response.errorBody()?.string() ?: "Kiralama sonlandırılamadı"
                    showError(errMsg)
                }
            } catch (e: Exception) {
                showError(e.message ?: "Bağlantı hatası oluştu")
            }
        }
    }

    private fun showError(message: String) {
        val parsedMsg = try {
            val jsonObject = org.json.JSONObject(message)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            message
        }
        _state.value = _state.value.copy(error = parsedMsg, isEndingRental = false)
        viewModelScope.launch {
            _effect.send(ActiveRentalEffect.ShowError(parsedMsg))
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        locationJob?.cancel()
    }
}
