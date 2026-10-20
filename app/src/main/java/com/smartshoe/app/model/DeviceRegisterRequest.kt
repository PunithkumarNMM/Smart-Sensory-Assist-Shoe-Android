package com.smartshoe.app.model

data class DeviceRegisterRequest(
    val shoeName: String,
    val esp32Mac: String
)