package com.smartshoe.app.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)