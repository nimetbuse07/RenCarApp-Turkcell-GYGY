package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VehicleEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VehicleIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VehicleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VehicleState())
    val state: StateFlow<VehicleState> = _state.asStateFlow()

    private val _effect = Channel<VehicleEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Compatibility flows for MainDashboardScreen before its refactoring
    private val _vehiclesCompatibility = MutableStateFlow<List<VehicleResponseDto>>(emptyList())
    val vehicles: StateFlow<List<VehicleResponseDto>> = _vehiclesCompatibility.asStateFlow()

    private val _isLoadingCompatibility = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoadingCompatibility.asStateFlow()

    private val _errorCompatibility = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _errorCompatibility.asStateFlow()

    fun fetchVehicles(segment: String? = null) {
        onIntent(VehicleIntent.FetchVehicles(segment))
    }

    fun onIntent(intent: VehicleIntent) {
        when (intent) {
            is VehicleIntent.FetchVehicles -> {
                fetchVehiclesInternal(intent.segment)
            }
            is VehicleIntent.SelectVehicle -> {
                fetchVehicleDetail(intent.vehicleId)
            }
            is VehicleIntent.ClearSelectedVehicle -> {
                _state.value = _state.value.copy(selectedVehicle = null)
            }
            is VehicleIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
                _errorCompatibility.value = null
            }
        }
    }

    private fun fetchVehiclesInternal(segment: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            _isLoadingCompatibility.value = true
            _errorCompatibility.value = null
            Log.d("VehicleViewModel", "Fetching vehicles for segment: $segment")
            val result = vehicleRepository.getVehicles(segment = segment, includeBusy = "true")
            result.onSuccess { list ->
                Log.d("VehicleViewModel", "Successfully fetched ${list.size} vehicles")
                _state.value = _state.value.copy(vehicles = list, isLoading = false)
                _vehiclesCompatibility.value = list
                _isLoadingCompatibility.value = false
            }.onFailure { exception ->
                Log.e("VehicleViewModel", "Error fetching vehicles: ${exception.message}", exception)
                val errMsg = exception.message ?: "Araçlar yüklenirken bir hata oluştu"
                _state.value = _state.value.copy(error = errMsg, isLoading = false)
                _isLoadingCompatibility.value = false
                _errorCompatibility.value = errMsg
                _effect.send(VehicleEffect.ShowError(errMsg))
            }
        }
    }

    private fun fetchVehicleDetail(vehicleId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDetailLoading = true, error = null)
            Log.d("VehicleViewModel", "Fetching vehicle detail for id: $vehicleId")
            val result = vehicleRepository.getVehicle(vehicleId)
            result.onSuccess { vehicle ->
                Log.d("VehicleViewModel", "Successfully fetched vehicle detail for ${vehicle.brand} ${vehicle.model}")
                _state.value = _state.value.copy(selectedVehicle = vehicle, isDetailLoading = false)
            }.onFailure { exception ->
                Log.e("VehicleViewModel", "Error fetching vehicle detail: ${exception.message}", exception)
                val errMsg = exception.message ?: "Araç detayı yüklenirken bir hata oluştu"
                _state.value = _state.value.copy(error = errMsg, isDetailLoading = false)
                _effect.send(VehicleEffect.ShowError(errMsg))
            }
        }
    }
}
