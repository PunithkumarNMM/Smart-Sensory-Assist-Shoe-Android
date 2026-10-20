package com.smartshoe.app.model

data class SOSResponse(
    val success: Boolean,
    val message: String,
    val whatsapp: WhatsAppResult? = null
)

data class WhatsAppResult(
    val success: Boolean? = null,
    val error: String? = null,
    val messageId: String? = null
)
