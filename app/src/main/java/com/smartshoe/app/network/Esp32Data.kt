package com.smartshoe.app.network

data class Esp32Data(

    val status: String,

    val distance: Int,

    val fall: Boolean,

    val emergency: Boolean,

    val latitude: Double,

    val longitude: Double,

    val safetyScore: Int

)