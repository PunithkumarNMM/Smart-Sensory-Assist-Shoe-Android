package com.smartshoe.app.model

data class GuardianResponse(
    val success: Boolean,
    val count: Int = 0,
    val message: String = "",
    val data: List<Guardian>
)