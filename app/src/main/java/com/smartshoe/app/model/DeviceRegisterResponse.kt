package com.smartshoe.app.model

data class DeviceRegisterResponse(
    val success: Boolean,
    val message: String,
    val device: Device
)