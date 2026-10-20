package com.smartshoe.app.gps

data class LocationResponse(
    val success: Boolean,
    val location: LocationData
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val accuracy: Double,
    val createdAt: String
)