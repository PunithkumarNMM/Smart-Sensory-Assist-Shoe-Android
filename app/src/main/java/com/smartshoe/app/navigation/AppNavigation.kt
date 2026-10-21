
package com.smartshoe.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.smartshoe.app.ai.AIDetectionScreen
import com.smartshoe.app.camera.CameraScreen
import com.smartshoe.app.device.DeviceScreen
import com.smartshoe.app.device.RegisterDeviceScreen
import com.smartshoe.app.forgotpassword.ForgotPasswordScreen
import com.smartshoe.app.geofence.GeofenceScreen
import com.smartshoe.app.gps.GpsScreen
import com.smartshoe.app.guardian.AddGuardianScreen
import com.smartshoe.app.guardian.EditGuardianScreen
import com.smartshoe.app.guardian.GuardianScreen
import com.smartshoe.app.history.HistoryScreen
import com.smartshoe.app.login.LoginScreen
import com.smartshoe.app.notification.NotificationScreen
import com.smartshoe.app.onboarding.OnboardingScreen
import com.smartshoe.app.profile.ProfileScreen
import com.smartshoe.app.register.RegisterScreen
import com.smartshoe.app.settings.ChangePasswordScreen
import com.smartshoe.app.settings.SettingsScreen
import com.smartshoe.app.sos.SosScreen
import com.smartshoe.app.splash.SplashScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash",

        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeIn(
                animationSpec = tween(500)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(500)
            )
        },

        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeOut(
                animationSpec = tween(500)
            )
        },

        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeIn(
                animationSpec = tween(500)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(500)
            )
        },

        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeOut(
                animationSpec = tween(500)
            )
        }
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("forgotPassword") {
            ForgotPasswordScreen(navController)
        }

        composable("dashboard") {
            MainScreen()
        }

        composable("registerDevice") {
            RegisterDeviceScreen(navController)
        }

        composable("deviceInfo") {
            DeviceScreen(navController)
        }

        composable("camera") {
            CameraScreen(navController)
        }

        composable("gps") {
            GpsScreen(navController)
        }

        composable("geofence") {
            GeofenceScreen(navController)
        }

        composable("sos") {
            SosScreen(navController)
        }

        composable("notifications") {
            NotificationScreen(navController)
        }

        composable("history") {
            HistoryScreen(navController)
        }

        composable("guardians") {
            GuardianScreen(navController)
        }

        composable("addGuardian") {
            AddGuardianScreen(navController)
        }

        composable("editGuardian/{guardianId}") {
            val guardianId =
                it.arguments?.getString("guardianId") ?: ""

            EditGuardianScreen(
                navController,
                guardianId
            )
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("aiDetection") {
            AIDetectionScreen(navController)
        }

        composable("settings") {
            SettingsScreen(navController)
        }

        composable("changePassword") {
            ChangePasswordScreen(navController)
        }
    }
}
