package com.nimetatila.rencarapp_turkcell_gygy.ui.contract

import androidx.compose.runtime.Stable

@Stable
data class LoginState(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCodeSent: Boolean = false
)

sealed interface LoginIntent {
    data class PhoneChanged(val phone: String) : LoginIntent
    object SendOtp : LoginIntent
    object ClearError : LoginIntent
}

sealed interface LoginEffect {
    data class NavigateToVerify(val phoneNumber: String) : LoginEffect
}
