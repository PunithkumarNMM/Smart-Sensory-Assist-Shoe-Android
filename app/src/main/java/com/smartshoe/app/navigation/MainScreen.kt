package com.smartshoe.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    Surface(
        color = Color(0xFFF7F9FC)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(navController = bottomNavController)
            }
        ) { padding ->
            BottomNavGraph(
                navController = bottomNavController,
                rootNavController = rootNavController,
                modifier = Modifier.padding(
                    bottom = padding.calculateBottomPadding()
                )
            )
        }
    }
}