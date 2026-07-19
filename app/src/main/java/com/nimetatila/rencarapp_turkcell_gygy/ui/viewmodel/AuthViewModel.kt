package com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val message: String? = null) : AuthState
    data class Error(val message: String) : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userFullName = MutableStateFlow<String>("")
    val userFullName: StateFlow<String> = _userFullName.asStateFlow()

    private val _userPhone = MutableStateFlow<String>("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userRole = MutableStateFlow<String>("")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    val accessToken: StateFlow<String?> = authRepository.accessToken.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            authRepository.userFullName.collect { _userFullName.value = it ?: "" }
        }
        viewModelScope.launch {
            authRepository.userPhone.collect { _userPhone.value = it ?: "" }
        }
        viewModelScope.launch {
            authRepository.userRole.collect { _userRole.value = it ?: "" }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun register(email: String, password: String, fullName: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val formattedPhone = formatPhone(phone)
                val response = authRepository.register(email, password, fullName, formattedPhone)
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
                    _authState.value = AuthState.Success("Kayıt başarılı")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kayıt başarısız"
                    _authState.value = AuthState.Error(parseError(errorMsg))
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Bağlantı hatası oluştu")
            }
        }
    }

    fun login(phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val formattedPhone = formatPhone(phone)
                val response = authRepository.login(formattedPhone)
                if (response.isSuccessful && response.body() != null) {
                    _authState.value = AuthState.Success(response.body()!!.message)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Giriş kodu gönderilemedi"
                    _authState.value = AuthState.Error(parseError(errorMsg))
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Bağlantı hatası oluştu")
            }
        }
    }

    fun verifyOtp(phone: String, code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val formattedPhone = formatPhone(phone)
                val response = authRepository.verifyOtp(formattedPhone, code)
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
                    _authState.value = AuthState.Success("Giriş başarılı")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Kod doğrulanamadı"
                    _authState.value = AuthState.Error(parseError(errorMsg))
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Bağlantı hatası oluştu")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (e: Exception) {
                // Ignore api logout error, we still clear local preference
            }
            authRepository.clearAuthData()
            _authState.value = AuthState.Idle
            onSuccess()
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            try {
                val response = authRepository.getMe()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    authRepository.updateRole(user.role)
                }
            } catch (e: Exception) {
                // Profile fetch failed
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
