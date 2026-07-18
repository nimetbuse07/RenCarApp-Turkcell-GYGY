package com.nimetatila.rencarapp_turkcell_gygy.data.card

import retrofit2.Response
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardApi: CardApi
) : CardRepository {

    override suspend fun getCards(): Response<List<CardResponseDto>> {
        return cardApi.getCards()
    }

    override suspend fun createCard(request: CreateCardDto): Response<CardResponseDto> {
        return cardApi.createCard(request)
    }

    override suspend fun setDefaultCard(id: String): Response<CardResponseDto> {
        return cardApi.setDefaultCard(id)
    }
}
