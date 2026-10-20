package com.smartshoe.app.dashboard

data class DashboardResponse(
    val success: Boolean,
    val data: DashboardData
)

data class DashboardData(
    val name: String,
    val email: String,
    val profileImage: String,
    val deviceStatus: String,
    val battery: Int,
    val lastSync: String,
    val location: String
)