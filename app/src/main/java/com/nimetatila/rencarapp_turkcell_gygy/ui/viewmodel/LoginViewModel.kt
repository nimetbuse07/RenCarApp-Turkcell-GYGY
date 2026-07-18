package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LoginEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LoginIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.PhoneChanged -> {
                _state.value = _state.value.copy(phoneNumber = intent.phone)
            }
            is LoginIntent.SendOtp -> {
                sendOtp()
            }
            is LoginIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun sendOtp() {
        val phone = _state.value.phoneNumber
        if (phone.isBlank()) {
            _state.value = _state.value.copy(error = "Lütfen telefon numaranızı girin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val formattedPhone = formatPhone(phone)
                val response = authRepository.login(formattedPhone)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(isLoading = false, isCodeSent = true)
                    _effect.send(LoginEffect.NavigateToVerify(formattedPhone))
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Giriş kodu gönderilemedi"
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
