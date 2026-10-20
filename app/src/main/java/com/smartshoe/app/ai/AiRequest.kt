package com.smartshoe.app.ai

data class AiRequest(
    val prediction: String,
    val confidence: Double
)