package com.nimetatila.rencarapp_turkcell_gygy.data.vehicle

import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val vehicleApi: VehicleApi
) : VehicleRepository {
    override suspend fun getVehicles(
        type: String?,
        segment: String?,
        includeBusy: String?
    ): Result<List<VehicleResponseDto>> {
        return try {
            val response = vehicleApi.getVehicles(type = type, segment = segment, includeBusy = includeBusy, limit = 100)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVehicle(id: String): Result<VehicleResponseDto> {
        return try {
            val response = vehicleApi.getVehicle(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
