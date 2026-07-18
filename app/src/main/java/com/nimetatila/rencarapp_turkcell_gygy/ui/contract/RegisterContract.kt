package com.nimetatila.rencarapp_turkcell_gygy.ui.contract

import androidx.compose.runtime.Stable

@Stable
data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val referralCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed interface RegisterIntent {
    data class FullNameChanged(val value: String) : RegisterIntent
    data class EmailChanged(val value: String) : RegisterIntent
    data class PhoneChanged(val value: String) : RegisterIntent
    data class PasswordChanged(val value: String) : RegisterIntent
    data class ReferralCodeChanged(val value: String) : RegisterIntent
    object TogglePasswordVisibility : RegisterIntent
    object Register : RegisterIntent
    object ClearError : RegisterIntent
}

sealed interface RegisterEffect {
    object NavigateToLicenseVerification : RegisterEffect
}
