
package com.smartshoe.app.geofence

import android.content.Context

data class SafeZone(
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)

class GeofencePreferences(context: Context) {

    private val prefs = context.getSharedPreferences(
        "smart_shoe_safe_zone",
        Context.MODE_PRIVATE
    )

    fun save(zone: SafeZone) {
        prefs.edit()
            .putFloat("latitude", zone.latitude.toFloat())
            .putFloat("longitude", zone.longitude.toFloat())
            .putFloat("radius", zone.radiusMeters)
            .putBoolean("enabled", true)
            .apply()
    }

    fun get(): SafeZone? {
        if (!prefs.getBoolean("enabled", false)) return null

        return SafeZone(
            latitude = prefs.getFloat("latitude", 0f).toDouble(),
            longitude = prefs.getFloat("longitude", 0f).toDouble(),
            radiusMeters = prefs.getFloat("radius", 100f)
        )
    }

    fun isEnabled(): Boolean =
        prefs.getBoolean("enabled", false)

    fun disable() {
        prefs.edit().putBoolean("enabled", false).apply()
    }
}
