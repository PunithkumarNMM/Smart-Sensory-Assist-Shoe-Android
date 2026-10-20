package com.smartshoe.app.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)