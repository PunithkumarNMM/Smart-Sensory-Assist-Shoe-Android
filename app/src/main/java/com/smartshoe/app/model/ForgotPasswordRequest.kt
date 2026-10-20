package com.smartshoe.app.model

data class ForgotPasswordRequest(
    val email: String,
    val newPassword: String
)