package com.nimetatila.rencarapp_turkcell_gygy.ui.intent

import androidx.compose.runtime.Stable
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalStatsResponseDto

@Stable
data class HistoryState(
    val rentals: List<RentalResponseDto> = emptyList(),
    val stats: RentalStatsResponseDto? = null,
    val isLoadingRentals: Boolean = false,
    val isLoadingStats: Boolean = false,
    val rentalsError: String? = null,
    val statsError: String? = null
)

sealed interface HistoryIntent {
    object LoadHistory : HistoryIntent
    data class LoadStats(val month: String?) : HistoryIntent
    object Refresh : HistoryIntent
}

sealed interface HistoryEffect {
    data class ShowMessage(val message: String) : HistoryEffect
}
