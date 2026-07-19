package com.nimetatila.rencarapp_turkcell_gygy.data.wallet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {

    @GET("wallet")
    suspend fun getWallet(): Response<WalletResponseDto>

    @POST("wallet/topup")
    suspend fun topupWallet(
        @Body request: TopupDto
    ): Response<WalletResponseDto>
}
