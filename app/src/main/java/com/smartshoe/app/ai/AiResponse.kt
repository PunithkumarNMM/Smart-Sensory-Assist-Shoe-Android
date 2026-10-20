package com.smartshoe.app.ai

data class AiResponse(
    val success: Boolean,
    val prediction: String,
    val confidence: Double,
    val emergency: Boolean
)