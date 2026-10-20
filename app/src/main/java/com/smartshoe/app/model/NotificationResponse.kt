package com.smartshoe.app.model

data class NotificationResponse(
    val success: Boolean,
    val data: List<Notification>
)