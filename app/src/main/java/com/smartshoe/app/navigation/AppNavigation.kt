package com.smartshoe.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartshoe.app.guardian.GuardianScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ... Existing routes (splash, login, registerDevice, dashboard) ...

        composable("dashboard") {
            MainScreen(rootNavController = navController)
        }

        // 👈 ADD GUARDIAN ROUTE HERE
        composable("guardian") {
            GuardianScreen(navController = navController)
        }
    }
}