package com.nimetatila.rencarapp_turkcell_gygy.data.card

import retrofit2.Response

interface CardRepository {
    suspend fun getCards(): Response<List<CardResponseDto>>
    suspend fun createCard(request: CreateCardDto): Response<CardResponseDto>
    suspend fun setDefaultCard(id: String): Response<CardResponseDto>
}
