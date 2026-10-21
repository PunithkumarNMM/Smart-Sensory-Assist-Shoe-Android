package com.smartshoe.app.model

data class SOSRequest(
    val deviceId: String,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val message: String? = "Emergency SOS Triggered!"
)