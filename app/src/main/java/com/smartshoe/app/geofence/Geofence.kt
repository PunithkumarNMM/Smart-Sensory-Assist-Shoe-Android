package com.smartshoe.app.geofence

data class Geofence(
    val _id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double,
    val startTime: String,
    val endTime: String,
    val enabled: Boolean
)