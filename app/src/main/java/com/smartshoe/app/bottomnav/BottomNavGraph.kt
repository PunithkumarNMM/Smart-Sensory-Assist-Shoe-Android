package com.smartshoe.app.bottomnav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartshoe.app.ai.AIDetectionScreen
import com.smartshoe.app.camera.CameraScreen
import com.smartshoe.app.dashboard.DashboardScreen
import com.smartshoe.app.device.DeviceScreen
import com.smartshoe.app.device.RegisterDeviceScreen
import com.smartshoe.app.gps.GpsScreen
import com.smartshoe.app.guardian.AddGuardianScreen
import com.smartshoe.app.guardian.EditGuardianScreen
import com.smartshoe.app.guardian.GuardianScreen
import com.smartshoe.app.history.HistoryScreen
import com.smartshoe.app.notification.NotificationScreen
import com.smartshoe.app.profile.ProfileScreen
import com.smartshoe.app.settings.ChangePasswordScreen
import com.smartshoe.app.settings.SettingsScreen
import com.smartshoe.app.sos.SosScreen
import com.smartshoe.app.geofence.GeofenceScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("gps") {
            GpsScreen(navController)
        }

        composable("camera") {
            CameraScreen(navController)
        }
        composable("geofence") {
            GeofenceScreen()
        }
        composable("history") {
            HistoryScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("registerDevice") {
            RegisterDeviceScreen(navController)
        }

        composable("deviceInfo") {
            DeviceScreen(navController)
        }

        composable("aiDetection") {
            AIDetectionScreen(navController)
        }

        composable("sos") {
            SosScreen(navController)
        }

        composable("notifications") {
            NotificationScreen(navController)
        }

        composable("guardians") {
            GuardianScreen(navController)
        }

        composable("addGuardian") {
            AddGuardianScreen(navController)
        }

        composable("editGuardian/{guardianId}") { backStackEntry ->

            val guardianId =
                backStackEntry.arguments?.getString("guardianId") ?: ""

            EditGuardianScreen(
                navController = navController,
                guardianId = guardianId
            )
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("changePassword") {
            ChangePasswordScreen(navController)
        }
    }
}