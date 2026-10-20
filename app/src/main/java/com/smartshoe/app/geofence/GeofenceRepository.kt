package com.smartshoe.app.geofence

import com.smartshoe.app.network.RetrofitClient

class GeofenceRepository {

    suspend fun getGeofences(): List<Geofence> {
        val response = RetrofitClient.api.getGeofences()

        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()?.data.orEmpty()
        }

        return emptyList()
    }
}