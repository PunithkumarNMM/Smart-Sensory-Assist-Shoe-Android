package com.smartshoe.app.device

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager

@Composable
fun DeviceScreen(navController: NavController) {
    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    var device by remember { mutableStateOf<DeviceData?>(null) }

    LaunchedEffect(Unit) {
        val deviceId = session.getDeviceId()
        if (!deviceId.isNullOrEmpty()) {
            try {
                val response = RetrofitClient.api.getDeviceStatus(deviceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    device = response.body()?.data
                }
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Welcome Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "👋 Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Your Smart Shoe is Ready",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Device Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Device Summary",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Summary 2x2 Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.BatteryFull,
                        value = "${device?.battery ?: 0}%",
                        label = "Battery"
                    )
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Wifi,
                        value = device?.status ?: "Offline",
                        label = "Status"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocationOn,
                        value = if (device?.gps == true) "Active" else "Unknown",
                        label = "Location"
                    )
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Security,
                        value = "Protected",
                        label = "Safety"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Access Section Header
        Text(
            text = "Quick Access",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccountCircle,
                title = "Profile",
                onClick = { navController.navigate("profile") }
            )
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Devices,
                title = "Register Device",
                onClick = { navController.navigate("registerDevice") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocationOn,
                title = "Live GPS",
                onClick = { navController.navigate("gps") }
            )
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.PhotoCamera,
                title = "Camera",
                onClick = { navController.navigate("camera") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Row 3 (Additional Options)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.History,
                title = "History Log",
                onClick = { navController.navigate("history") }
            )
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Warning,
                title = "SOS / Emergency",
                onClick = { navController.navigate("sos") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid Row 4 (Additional Options)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Notifications,
                title = "Notifications",
                onClick = { navController.navigate("notifications") }
            )
            QuickAccessTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.SupervisorAccount,
                title = "Guardian Info",
                onClick = { navController.navigate("guardian") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Detailed Technical Specifications List Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Device Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DetailRow(label = "Shoe Name", value = device?.shoeName ?: "Not Available")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(label = "Device ID", value = device?.deviceId ?: "Not Available")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(label = "ESP32 MAC", value = device?.mac ?: "Not Available")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(label = "Firmware", value = device?.firmware ?: "v1.0.0")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(label = "Last Seen", value = device?.lastSeen ?: "Never")
            }
        }
    }
}

@Composable
fun SummaryTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickAccessTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}