package com.smartshoe.app.model

data class SOSRequest(
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val message: String
)