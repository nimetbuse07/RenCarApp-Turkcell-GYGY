package com.nimetatila.rencarapp_turkcell_gygy.data.wallet
import retrofit2.Response

interface WalletRepository {
    suspend fun getWallet(): Response<WalletResponseDto>
    suspend fun topupWallet(request: TopupDto): Response<WalletResponseDto>
}
