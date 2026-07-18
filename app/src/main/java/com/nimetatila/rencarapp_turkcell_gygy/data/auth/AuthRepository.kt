package com.nimetatila.rencarapp_turkcell_gygy.data.auth

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthRepository {

    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    val userRole: Flow<String?>
    val userPhone: Flow<String?>
    val userFullName: Flow<String?>

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        referralCode: String? = null
    ): Response<AuthResponse>

    suspend fun login(
        phone: String
    ): Response<OtpResponse>

    suspend fun verifyOtp(
        phone: String,
        code: String
    ): Response<AuthResponse>

    suspend fun logout(): Response<LogoutResponse>

    suspend fun getMe(): Response<UserDto>

    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        phone: String?,
        fullName: String,
        role: String
    )

    suspend fun clearAuthData()

    suspend fun updateRole(role: String)

    suspend fun refreshSession(): Response<AuthResponse>
}
