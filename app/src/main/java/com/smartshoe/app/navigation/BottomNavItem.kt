package com.smartshoe.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        "dashboard",
        "Home",
        Icons.Default.Home
    )

    object GPS : BottomNavItem(
        "gps",
        "GPS",
        Icons.Default.LocationOn
    )

    object SOS : BottomNavItem(
        "sos",
        "SOS",
        Icons.Default.Warning
    )

    object Notifications : BottomNavItem(
        "notifications",
        "Notifications",
        Icons.Default.Notifications
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.AccountCircle
    )
}