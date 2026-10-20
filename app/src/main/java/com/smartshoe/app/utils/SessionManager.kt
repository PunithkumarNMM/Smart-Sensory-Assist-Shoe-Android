package com.smartshoe.app.utils

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("smartshoe", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    // ==========================
    // DEVICE ID
    // ==========================

    fun saveDeviceId(deviceId: String) {
        prefs.edit().putString("deviceId", deviceId).apply()
    }

    fun getDeviceId(): String? {
        return prefs.getString("deviceId", null)
    }

    fun clearDevice() {
        prefs.edit().remove("deviceId").apply()
    }

    // ==========================
    // GUARDIAN PHONE NUMBER
    // ==========================

    fun saveGuardianPhone(phone: String) {
        prefs.edit().putString("guardianPhone", phone).apply()
    }

    fun getGuardianPhone(): String? {
        return prefs.getString("guardianPhone", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}