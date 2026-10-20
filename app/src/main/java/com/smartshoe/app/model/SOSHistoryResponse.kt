package com.smartshoe.app.model

data class SOSHistoryResponse(
    val success: Boolean,
    val count: Int,
    val data: List<SOSHistory>
)