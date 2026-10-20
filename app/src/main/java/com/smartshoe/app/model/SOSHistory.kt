package com.smartshoe.app.model

data class SOSHistory(
    val _id: String,
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val message: String,
    val createdAt: String
)
