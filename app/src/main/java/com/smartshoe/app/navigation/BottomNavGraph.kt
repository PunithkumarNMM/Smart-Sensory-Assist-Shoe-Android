package com.smartshoe.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartshoe.app.device.DeviceScreen
import com.smartshoe.app.gps.GpsScreen
import com.smartshoe.app.notification.NotificationScreen
import com.smartshoe.app.profile.ProfileScreen
import com.smartshoe.app.sos.SosScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") {
            DeviceScreen(navController = rootNavController)
        }
        composable("gps") {
            GpsScreen(navController)
        }
        composable("sos") {
            SosScreen(navController)
        }
        composable("notifications") {
            NotificationScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
    }
}