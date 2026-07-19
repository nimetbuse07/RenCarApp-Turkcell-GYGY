package com.nimetatila.rencarapp_turkcell_gygy.ui.intent

import androidx.compose.runtime.Stable

@Stable
data class VerifyState(
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed interface VerifyIntent {
    data class CodeChanged(val value: String) : VerifyIntent
    data class Verify(val phone: String) : VerifyIntent
    object ClearError : VerifyIntent
}

sealed interface VerifyEffect {
    object NavigateToDashboard : VerifyEffect
}
