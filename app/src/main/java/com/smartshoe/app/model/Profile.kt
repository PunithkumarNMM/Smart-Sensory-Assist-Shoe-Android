package com.smartshoe.app.model

data class Profile(
    val id: String? = null,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String? = null,
    val profileImage: String = ""
)