package com.smartshoe.app.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: User?
)