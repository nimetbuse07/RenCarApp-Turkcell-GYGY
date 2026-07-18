package com.nimetatila.rencarapp_turkcell_gygy.data.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: OtpRequest
    ): Response<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("auth/me")
    suspend fun getMe(): Response<UserDto>

    @POST("auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): Response<AuthResponse>
}
