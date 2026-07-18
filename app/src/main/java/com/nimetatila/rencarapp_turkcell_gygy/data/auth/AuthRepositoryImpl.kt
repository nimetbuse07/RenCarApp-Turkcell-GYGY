package com.nimetatila.rencarapp_turkcell_gygy.data.auth

import com.nimetatila.rencarapp_turkcell_gygy.data.preferences.AuthPreferencesRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.flow.first

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val authPreferencesRepository: AuthPreferencesRepository
) : AuthRepository {

    override val accessToken: Flow<String?> = authPreferencesRepository.accessToken
    override val refreshToken: Flow<String?> = authPreferencesRepository.refreshToken
    override val userRole: Flow<String?> = authPreferencesRepository.userRole
    override val userPhone: Flow<String?> = authPreferencesRepository.userPhone
    override val userFullName: Flow<String?> = authPreferencesRepository.userFullName

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        referralCode: String?
    ): Response<AuthResponse> {
        return authApi.register(RegisterRequest(email, password, fullName, phone, referralCode))
    }

    override suspend fun login(phone: String): Response<OtpResponse> {
        return authApi.login(OtpRequest(phone))
    }

    override suspend fun verifyOtp(phone: String, code: String): Response<AuthResponse> {
        return authApi.verifyOtp(VerifyOtpRequest(phone, code))
    }

    override suspend fun logout(): Response<LogoutResponse> {
        return authApi.logout()
    }

    override suspend fun getMe(): Response<UserDto> {
        return authApi.getMe()
    }

    override suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        phone: String?,
        fullName: String,
        role: String
    ) {
        authPreferencesRepository.saveAuthData(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            phone = phone ?: "",
            fullName = fullName,
            role = role
        )
    }

    override suspend fun clearAuthData() {
        authPreferencesRepository.clearAuthData()
    }

    override suspend fun updateRole(role: String) {
        authPreferencesRepository.updateRole(role)
    }

    override suspend fun refreshSession(): Response<AuthResponse> {
        val currentRefresh = refreshToken.first() ?: throw Exception("Refresh token bulunamadı")
        val response = authApi.refresh(RefreshTokenRequest(currentRefresh))
        if (response.isSuccessful && response.body() != null) {
            val authBody = response.body()!!
            saveAuthData(
                accessToken = authBody.accessToken,
                refreshToken = authBody.refreshToken,
                userId = authBody.user.id,
                email = authBody.user.email,
                phone = authBody.user.phone ?: "",
                fullName = authBody.user.fullName,
                role = authBody.user.role
            )
        }
        return response
    }
}
