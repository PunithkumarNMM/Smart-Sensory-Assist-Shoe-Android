
package com.smartshoe.app.bottomnav

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
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
fun BottomBar(navController: NavHostController) {

    val items = listOf(
        BottomItem("dashboard", "Home", Icons.Default.Home),
        BottomItem("gps", "GPS", Icons.Default.LocationOn),
        BottomItem("geofence", "Safe Zone", Icons.Default.MyLocation),
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
                        contentDescription = item.title,
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
