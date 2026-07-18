package com.nimetatila.rencarapp_turkcell_gygy.data.vehicle

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleApi {
    @GET("vehicles")
    suspend fun getVehicles(
        @Query("type") type: String? = null,
        @Query("segment") segment: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("includeBusy") includeBusy: String? = null
    ): List<VehicleResponseDto>

    @GET("vehicles/{id}")
    suspend fun getVehicle(
        @Path("id") id: String
    ): VehicleResponseDto
}
