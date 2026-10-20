package com.smartshoe.app.model

data class UploadResponse(
    val success: Boolean,
    val message: String,
    val imageUrl: String
)