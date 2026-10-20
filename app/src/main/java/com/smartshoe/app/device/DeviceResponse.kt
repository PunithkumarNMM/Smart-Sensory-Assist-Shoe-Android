package com.smartshoe.app.device

data class DeviceResponse(
    val success: Boolean,
    val data: DeviceData
)

data class DeviceData(
    val deviceId: String,
    val shoeName: String,
    val battery: Int,
    val status: String,
    val firmware: String,
    val gps: Boolean,
    val camera: Boolean,
    val lastSeen: String,
    val mac: String
)