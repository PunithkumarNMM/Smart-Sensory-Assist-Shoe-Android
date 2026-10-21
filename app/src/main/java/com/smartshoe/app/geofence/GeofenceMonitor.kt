
package com.smartshoe.app.geofence

import android.content.Context
import com.smartshoe.app.gps.LocationResponse
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

sealed class GeofenceResult {
    data class Inside(val distanceMeters: Double) : GeofenceResult()
    data class Outside(val distanceMeters: Double) : GeofenceResult()
    data class Error(val message: String) : GeofenceResult()
}

class GeofenceMonitor(context: Context) {

    private val appContext = context.applicationContext
    private val preferences = GeofencePreferences(appContext)

    suspend fun checkOnce(deviceId: String): GeofenceResult =
        withContext(Dispatchers.IO) {
            try {
                val zone = preferences.get()
                    ?: return@withContext GeofenceResult.Error(
                        "Save and enable a safe zone first."
                    )

                if (deviceId.isBlank()) {
                    return@withContext GeofenceResult.Error(
                        "No shoe device is linked to this account."
                    )
                }

                val response = RetrofitClient.api
                    .getLiveLocation(deviceId)

                if (!response.isSuccessful) {
                    return@withContext GeofenceResult.Error(
                        "GPS server returned HTTP ${response.code()}."
                    )
                }

                val body: LocationResponse = response.body()
                    ?: return@withContext GeofenceResult.Error(
                        "GPS response was empty."
                    )

                if (!body.success) {
                    return@withContext GeofenceResult.Error(
                        "The server did not provide a valid GPS position."
                    )
                }

                val gps = body.location

                if (
                    !gps.latitude.isFinite() ||
                    !gps.longitude.isFinite() ||
                    gps.latitude !in -90.0..90.0 ||
                    gps.longitude !in -180.0..180.0 ||
                    (gps.latitude == 0.0 && gps.longitude == 0.0)
                ) {
                    return@withContext GeofenceResult.Error(
                        "The shoe GPS coordinates are invalid."
                    )
                }

                // Haversine distance in metres.
                val earthRadius = 6_371_000.0
                val lat1 = Math.toRadians(zone.latitude)
                val lat2 = Math.toRadians(gps.latitude)
                val deltaLat = Math.toRadians(gps.latitude - zone.latitude)
                val deltaLon = Math.toRadians(gps.longitude - zone.longitude)

                val a = sin(deltaLat / 2).pow(2) +
                        cos(lat1) * cos(lat2) *
                        sin(deltaLon / 2).pow(2)

                val distance = earthRadius * 2 * atan2(
                    sqrt(a.coerceIn(0.0, 1.0)),
                    sqrt((1.0 - a).coerceAtLeast(0.0))
                )

                if (distance > zone.radiusMeters) {
                    GeofenceResult.Outside(distance)
                } else {
                    GeofenceResult.Inside(distance)
                }
            } catch (e: Exception) {
                GeofenceResult.Error(
                    e.message ?: "Unable to check shoe GPS."
                )
            }
        }
}
