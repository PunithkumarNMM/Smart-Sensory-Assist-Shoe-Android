package com.smartshoe.app.bottomnav

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomItem(

    val route: String,

    val title: String,

    val icon: ImageVector

)

@Composable
fun BottomBar(
    navController: NavHostController
) {

    val items = listOf(

        BottomItem("dashboard", "Home", Icons.Default.Home),

        BottomItem("gps", "GPS", Icons.Default.LocationOn),

        BottomItem("camera", "Camera", Icons.Default.CameraAlt),

        BottomItem("history", "History", Icons.AutoMirrored.Filled.DirectionsWalk),

        BottomItem("profile", "Profile", Icons.Default.AccountCircle)

    )



    val currentRoute =
        navController.currentBackStackEntryAsState()
            .value?.destination?.route

    NavigationBar(

        containerColor = Color.White,

        tonalElevation = 10.dp

    ) {

        items.forEach { item ->

            val selected = currentRoute == item.route

            val scale by animateFloatAsState(

                targetValue = if (selected) 1.2f else 1f,

                label = "bottomBar"

            )

            NavigationBarItem(

                selected = selected,

                onClick = {

                    if (currentRoute != item.route) {

                        navController.navigate(item.route) {

                            launchSingleTop = true

                            restoreState = true

                        }

                    }

                },

                colors = NavigationBarItemDefaults.colors(

                    selectedIconColor = Color.White,

                    selectedTextColor = Color(0xFF1565C0),

                    indicatorColor = Color(0xFF1565C0),

                    unselectedIconColor = Color.Gray,

                    unselectedTextColor = Color.Gray

                ),

                icon = {

                    Icon(

                        imageVector = item.icon,

                        contentDescription = null,

                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale)

                    )

                },

                label = {

                    Text(item.title)

                }

            )

        }

    }

}