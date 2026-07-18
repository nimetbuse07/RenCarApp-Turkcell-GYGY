package com.nimetatila.rencarapp_turkcell_gygy.data.rental


import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RideLocationClient @Inject constructor(
    private val authRepository: AuthRepository
) {
    fun vehiclePositionStream(): Flow<VehiclePoint> = callbackFlow {
        var socket: Socket? = null
        var triedRefresh = false

        fun teardown() {
            socket?.let {
                it.off()
                it.disconnect()
                it.close()
            }
            socket = null
        }

        fun connectWith(token: String) {
            val opts = IO.Options().apply {
                auth = mapOf("token" to token)
                forceNew = true
                reconnection = true
            }
            val s = IO.socket(BASE_URL + NAMESPACE, opts)

            s.on(MY_VEHICLE_EVENT) { args ->
                parsePoint(args)?.let { trySend(it) }
            }
            s.on(Socket.EVENT_CONNECT_ERROR) {
                if (!triedRefresh) {
                    triedRefresh = true
                    launch {
                        try {
                            val refreshResponse = authRepository.refreshSession()
                            if (refreshResponse.isSuccessful) {
                                val fresh = authRepository.accessToken.first()
                                teardown()
                                if (fresh != null) {
                                    connectWith(fresh)
                                } else {
                                    close()
                                }
                            } else {
                                close()
                            }
                        } catch (e: Exception) {
                            close()
                        }
                    }
                }
            }
            socket = s
            s.connect()
        }

        launch {
            try {
                val token = authRepository.accessToken.first()
                if (token == null) {
                    close()
                } else {
                    connectWith(token)
                }
            } catch (e: Exception) {
                close()
            }
        }

        awaitClose { teardown() }
    }

    private fun parsePoint(args: Array<Any?>): VehiclePoint? {
        val root = args.getOrNull(0) as? JSONObject ?: return null
        val vehicle = root.optJSONObject("vehicle") ?: return null
        val lat = vehicle.optDouble("latitude", Double.NaN)
        val lng = vehicle.optDouble("longitude", Double.NaN)
        if (lat.isNaN() || lng.isNaN()) return null
        return VehiclePoint(latitude = lat, longitude = lng)
    }

    private companion object {
        const val BASE_URL = "https://rencarv2.halitkalayci.com"
        const val NAMESPACE = "/ws/locations"
        const val MY_VEHICLE_EVENT = "my-vehicle"
    }
}

data class VehiclePoint(
    val latitude: Double,
    val longitude: Double,
)
