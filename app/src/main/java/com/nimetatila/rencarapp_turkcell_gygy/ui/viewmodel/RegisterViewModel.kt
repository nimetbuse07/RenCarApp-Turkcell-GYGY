package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.RegisterEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.RegisterIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _effect = Channel<RegisterEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.FullNameChanged -> {
                _state.value = _state.value.copy(fullName = intent.value)
            }
            is RegisterIntent.EmailChanged -> {
                _state.value = _state.value.copy(email = intent.value)
            }
            is RegisterIntent.PhoneChanged -> {
                _state.value = _state.value.copy(phoneNumber = intent.value)
            }
            is RegisterIntent.PasswordChanged -> {
                _state.value = _state.value.copy(password = intent.value)
            }
            is RegisterIntent.ReferralCodeChanged -> {
                _state.value = _state.value.copy(referralCode = intent.value)
            }
            is RegisterIntent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(passwordVisible = !_state.value.passwordVisible)
            }
            is RegisterIntent.Register -> {
                register()
            }
            is RegisterIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun register() {
        val s = _state.value
        if (s.fullName.isBlank() || s.email.isBlank() || s.phoneNumber.isBlank() || s.password.isBlank()) {
            _state.value = _state.value.copy(error = "Lütfen tüm alanları doldurun")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val formattedPhone = formatPhone(s.phoneNumber)
                val referralCodeParam = s.referralCode.takeIf { it.isNotBlank() }
                val response = authRepository.register(
                    email = s.email,
                    password = s.password,
                    fullName = s.fullName,
                    phone = formattedPhone,
                    referralCode = referralCodeParam
                )
                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!
                    authRepository.saveAuthData(
                        accessToken = authBody.accessToken,
                        refreshToken = authBody.refreshToken,
                        userId = authBody.user.id,
                        email = authBody.user.email,
                        phone = authBody.user.phone ?: "",
                        fullName = authBody.user.fullName,
                        role = authBody.user.role
                    )
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                    _effect.send(RegisterEffect.NavigateToLicenseVerification)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kayıt başarısız"
                    _state.value = _state.value.copy(isLoading = false, error = parseError(errorMsg))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Bağlantı hatası oluştu")
            }
        }
    }

    private fun formatPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return when {
            digits.length == 10 -> "+90$digits"
            digits.length == 12 && digits.startsWith("90") -> "+$digits"
            else -> "+90$digits"
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
