package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.HistoryEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.HistoryIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.HistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VehicleRecordsViewModel @Inject constructor(
    private val rentalRepository: RentalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val _effect = Channel<HistoryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    fun onIntent(intent: HistoryIntent) {
        when (intent) {
            is HistoryIntent.LoadHistory -> {
                loadHistory()
            }
            is HistoryIntent.LoadStats -> {
                loadStats(intent.month)
            }
            is HistoryIntent.Refresh -> {
                loadData()
            }
        }
    }

    private fun loadData() {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        loadHistory()
        loadStats(currentMonth)
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingRentals = true, rentalsError = null)
            try {
                val response = rentalRepository.getRentals()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isLoadingRentals = false,
                        rentals = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kiralamalar yüklenemedi"
                    _state.value = _state.value.copy(
                        isLoadingRentals = false,
                        rentalsError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingRentals = false,
                    rentalsError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun loadStats(month: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingStats = true, statsError = null)
            try {
                val response = rentalRepository.getRentalStats(month)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isLoadingStats = false,
                        stats = response.body()!!
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "İstatistikler yüklenemedi"
                    _state.value = _state.value.copy(
                        isLoadingStats = false,
                        statsError = parseError(errorMsg)
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoadingStats = false,
                    statsError = e.message ?: "Bağlantı hatası oluştu"
                )
            }
        }
    }

    private fun parseError(jsonError: String): String {
        return try {
            val jsonObject = JSONObject(jsonError)
            jsonObject.optString("message", "Bir hata oluştu")
        } catch (e: Exception) {
            "Bir hata oluştu"
        }
    }
}
