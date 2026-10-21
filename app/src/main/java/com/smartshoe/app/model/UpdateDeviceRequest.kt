
package com.smartshoe.app.model

data class UpdateDeviceRequest(
    val deviceId: String,
    val fcmToken: String
)
