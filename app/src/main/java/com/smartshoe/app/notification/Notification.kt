package com.smartshoe.app.model

data class Notification(
    val _id: String,
    val deviceId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)