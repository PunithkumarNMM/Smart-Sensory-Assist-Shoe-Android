
package com.smartshoe.app.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.alpha

import androidx.compose.ui.draw.scale
data class DashboardItem(
    val title: String,

    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun DashboardScreen(navController: NavController) {
    val session = SessionManager(navController.context)

    val headerAlpha = remember { Animatable(0f) }
    val headerScale = remember { Animatable(0.85f) }
    var showCards by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerAlpha.animateTo(1f, animationSpec = tween(600))
        headerScale.animateTo(
            1f,
            animationSpec = tween(
                700,
                easing = FastOutSlowInEasing
            )
        )
        delay(250)
        showCards = true
    }

    var userName by remember { mutableStateOf("Loading...") }
    var battery by remember { mutableStateOf(0) }
    var deviceStatus by remember { mutableStateOf("Offline") }
    var location by remember { mutableStateOf("Unknown") }

    val items = listOf(
        DashboardItem("Profile", Icons.Default.AccountCircle),
        DashboardItem("Live GPS", Icons.Default.LocationOn),
        DashboardItem("Camera", Icons.Default.CameraAlt),
        DashboardItem("AI Detection", Icons.Default.Psychology),
        DashboardItem("SOS", Icons.Default.Warning),
        DashboardItem("Guardians", Icons.Default.People),
        DashboardItem(
            "History",
            Icons.AutoMirrored.Filled.DirectionsWalk
        ),
        DashboardItem("Notifications", Icons.Default.Notifications),
        DashboardItem("Settings", Icons.Default.Settings),
        DashboardItem("Register Device", Icons.Default.Devices)
    )


    LaunchedEffect(Unit) {
        try {
            val deviceId = session.getDeviceId()
            if (!deviceId.isNullOrEmpty()) {
                val response = RetrofitClient.api.getDashboard(deviceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    userName = data.name
                    battery = data.battery
                    deviceStatus = data.deviceStatus
                    location = data.location
                }
            } else {
                userName = "Welcome!"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            userName = "Welcome!"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Text(
            text = "👋 Welcome",
            modifier = Modifier
                .alpha(headerAlpha.value)
                .scale(headerScale.value),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userName,
            modifier = Modifier
                .alpha(headerAlpha.value)
                .scale(headerScale.value),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your Smart Shoe is Ready",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(22.dp))

        AnimatedVisibility(
            visible = showCards,
            enter = fadeIn(animationSpec = tween(700)) +
                    slideInVertically(
                        animationSpec = tween(700),
                        initialOffsetY = { it / 2 }
                    )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "Device Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusCard(
                            "Battery",
                            "$battery%",
                            Icons.Default.Battery6Bar
                        )
                        StatusCard(
                            "Status",
                            deviceStatus,
                            Icons.Default.Wifi
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatusCard(
                            "Location",
                            location,
                            Icons.Default.LocationOn
                        )
                        StatusCard(
                            "Safety",
                            "Protected",
                            Icons.Default.HealthAndSafety
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quick Access",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(items.size) { index ->
                val item = items[index]

                AnimatedVisibility(
                    visible = showCards,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = index * 80
                        )
                    ) + slideInVertically(
                        animationSpec = tween(
                            durationMillis = 600,
                            delayMillis = index * 80
                        ),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(165.dp)
                            .clickable {
                                when (item.title) {
                                    "Register Device" -> {
                                        navController.navigate("registerDevice")
                                    }
                                    "Profile" ->
                                        navController.navigate("profile")

                                    "Camera" ->
                                        navController.navigate("camera")

                                    "Live GPS" ->
                                        navController.navigate("gps")

                                    "SOS" ->
                                        navController.navigate("sos")

                                    "Notifications" ->
                                        navController.navigate("notifications")

                                    "History" ->
                                        navController.navigate("history")

                                    "Guardians" ->
                                        navController.navigate("guardians")

                                    "Settings" ->
                                        navController.navigate("settings")

                                    "AI Detection" ->
                                        navController.navigate("aiDetection")
                                }
                            },
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.12f
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
