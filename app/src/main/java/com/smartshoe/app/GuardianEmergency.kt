package com.smartshoe.app
import android.content.Context
import android.content.Intent
import android.net.Uri

object GuardianEmergency {

    fun callGuardian(
        context: Context,
        guardianPhone: String
    ) {
        val phone = guardianPhone.trim()

        if (phone.isEmpty()) {
            return
        }

        val intent = Intent(
            Intent.ACTION_DIAL,
            Uri.parse("tel:$phone")
        )

        context.startActivity(intent)
    }

    fun sendWhatsAppMessage(
        context: Context,
        guardianPhone: String,
        childName: String,
        emergencyType: String,
        latitude: Double?,
        longitude: Double?
    ) {
        val locationText =
            if (latitude != null && longitude != null) {
                "https://www.google.com/maps?q=$latitude,$longitude"
            } else {
                "Location is currently unavailable"
            }

        val message = """
            🚨 SMART SHOE EMERGENCY ALERT 🚨

            Child: $childName

            Emergency: $emergencyType

            Location:
            $locationText

            Please check the child immediately.
        """.trimIndent()

        val phone = guardianPhone
            .replace("+", "")
            .replace(" ", "")
            .replace("-", "")

        val whatsappUrl =
            "https://wa.me/$phone?text=${Uri.encode(message)}"

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(whatsappUrl)
        )

        context.startActivity(intent)
    }
}