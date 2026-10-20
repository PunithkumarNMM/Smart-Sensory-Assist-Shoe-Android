package com.smartshoe.app.model

data class Device(
    val _id: String,
    val deviceId: String,
    val shoeName: String,
    val esp32Mac: String,
    val parentId: String
)