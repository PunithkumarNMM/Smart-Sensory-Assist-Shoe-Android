package com.smartshoe.app.model

data class GuardianRequest(
    val name: String,
    val relationship: String,
    val phone: String,
    val email: String,
    val isPrimary: Boolean
)