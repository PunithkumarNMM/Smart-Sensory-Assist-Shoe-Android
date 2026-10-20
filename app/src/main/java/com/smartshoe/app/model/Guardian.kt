package com.smartshoe.app.model

data class Guardian(
    val _id: String,
    val userId: String,
    val name: String,
    val relationship: String,
    val phone: String,
    val email: String,
    val isPrimary: Boolean,
    val createdAt: String
)