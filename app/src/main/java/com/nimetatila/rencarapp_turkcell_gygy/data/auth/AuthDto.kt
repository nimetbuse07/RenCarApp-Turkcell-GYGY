package com.nimetatila.rencarapp_turkcell_gygy.data.auth

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val referralCode: String? = null
)

@Serializable
data class OtpRequest(
    val phone: String
)

@Serializable
data class OtpResponse(
    val message: String,
    val phone: String,
    val expiresAt: String
)

@Serializable
data class VerifyOtpRequest(
    val phone: String,
    val code: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val phone: String?,
    val fullName: String,
    val role: String,
    val referralCode: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class LogoutResponse(
    val message: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)
