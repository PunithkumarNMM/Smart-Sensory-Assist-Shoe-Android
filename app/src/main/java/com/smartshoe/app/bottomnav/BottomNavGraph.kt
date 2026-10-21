
package com.smartshoe.app.bottomnav

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.smartshoe.app.ai.AIDetectionScreen
import com.smartshoe.app.camera.CameraScreen
import com.smartshoe.app.dashboard.DashboardScreen
import com.smartshoe.app.device.DeviceScreen
import com.smartshoe.app.geofence.GeofenceScreen
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
import com.smartshoe.app.device.RegisterDeviceScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        Log.d("BOTTOM_NAV_ROUTE", "Current route: $currentRoute")
    }

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        // Dashboard — required as the start destination
        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("gps") {
            GpsScreen(navController)
        }

        composable("geofence") {
            GeofenceScreen(navController)
        }

        composable("camera") {
            CameraScreen(navController)
        }

        composable("history") {
            HistoryScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        // Temporary test screen to diagnose the white screen
        composable("registerDevice") {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.material3.MaterialTheme.colorScheme.background
            ) {
                RegisterDeviceScreen(navController)
            }
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
