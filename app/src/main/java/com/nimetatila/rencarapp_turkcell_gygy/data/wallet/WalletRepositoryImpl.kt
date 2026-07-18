package com.nimetatila.rencarapp_turkcell_gygy.data.wallet

import retrofit2.Response
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val walletApi: WalletApi
) : WalletRepository {

    override suspend fun getWallet(): Response<WalletResponseDto> {
        return walletApi.getWallet()
    }

    override suspend fun topupWallet(request: TopupDto): Response<WalletResponseDto> {
        return walletApi.topupWallet(request)
    }
}
