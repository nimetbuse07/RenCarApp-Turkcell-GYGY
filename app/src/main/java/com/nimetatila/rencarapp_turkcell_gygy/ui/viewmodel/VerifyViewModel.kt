package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.VerifyEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.VerifyIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.VerifyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VerifyState())
    val state: StateFlow<VerifyState> = _state.asStateFlow()

    private val _effect = Channel<VerifyEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: VerifyIntent) {
        when (intent) {
            is VerifyIntent.CodeChanged -> {
                if (intent.value.length <= 6) {
                    _state.value = _state.value.copy(code = intent.value)
                }
            }
            is VerifyIntent.Verify -> {
                verify(intent.phone)
            }
            is VerifyIntent.ClearError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun verify(phone: String) {
        val s = _state.value
        if (s.code.length != 6) {
            _state.value = _state.value.copy(error = "Lütfen 6 haneli doğrulama kodunu girin")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = authRepository.verifyOtp(phone, s.code)
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
                    _effect.send(VerifyEffect.NavigateToDashboard)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kod doğrulanamadı"
                    _state.value = _state.value.copy(isLoading = false, error = parseError(errorMsg))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Bağlantı hatası oluştu")
            }
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
