package com.smartshoe.app.geofence



data class GeofenceResponse(
    val success: Boolean,
    val count: Int = 0,
    val message: String = "",
    val data: List<Geofence>
)