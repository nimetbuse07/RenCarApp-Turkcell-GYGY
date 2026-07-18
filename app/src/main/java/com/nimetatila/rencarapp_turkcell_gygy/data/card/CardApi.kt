package com.nimetatila.rencarapp_turkcell_gygy.data.card

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CardApi {

    @GET("cards")
    suspend fun getCards(): Response<List<CardResponseDto>>

    @POST("cards")
    suspend fun createCard(
        @Body request: CreateCardDto
    ): Response<CardResponseDto>

    @PATCH("cards/{id}/default")
    suspend fun setDefaultCard(
        @Path("id") id: String
    ): Response<CardResponseDto>
}
