package com.smartshoe.app.sos

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.smartshoe.app.location.LocationHelper
import com.smartshoe.app.model.SOSRequest
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SosScreen(
    navController: NavController
) {
    val context = navController.context
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(3) }
    var startCountdown by remember { mutableStateOf(false) }

    val pulse = rememberInfiniteTransition(label = "pulse")

    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val flash by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )

    // Handle Countdown Timer logic
    LaunchedEffect(startCountdown) {
        if (!startCountdown) return@LaunchedEffect
        countdown = 3
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        startCountdown = false
        showDialog = true
    }

    // Permission launcher for Location access
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(
                context,
                "Location permission is required to attach GPS to SOS",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF330000),
                        Color(0xFF8B0000).copy(alpha = flash),
                        Color(0xFF111111)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "🚨 EMERGENCY",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Press only during a real emergency",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Pulse SOS Button Area
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(
                            Color.Red.copy(alpha = 0.25f),
                            CircleShape
                        )
                )

                Button(
                    modifier = Modifier.size(200.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    enabled = !loading && !startCountdown,
                    onClick = {
                        startCountdown = true
                    }
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    } else {
                        Text(
                            text = "SOS",
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Countdown Indicator and Cancel option
            AnimatedVisibility(visible = startCountdown) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Sending SOS in",
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$countdown",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { startCountdown = false },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("CANCEL")
                    }
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            // Action Features Checklist Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.12f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Emergency Actions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "📍 Live GPS Location Attachment", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "📲 Automatic Guardian Notification", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🚑 SOS Log Persistence", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🔔 Real-time Emergency Dispatch", color = Color.White)
                }
            }
        }
    }

    // Confirmation Alert Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("🚨 Confirm Emergency SOS")
            },
            text = {
                Text("Your live location will be shared with all registered guardians and emergency services.\n\nDo you want to proceed?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        loading = true

                        val session = SessionManager(context)
                        val deviceId = session.getDeviceId()

                        if (deviceId.isNullOrEmpty()) {
                            loading = false
                            Toast.makeText(
                                context,
                                "No registered device found! Please register a device first.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        // Verify Location Permission
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            loading = false
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            return@Button
                        }

                        // Fetch Location and Dispatch API call
                        LocationHelper.getCurrentLocation(
                            context,
                            onSuccess = { latitude, longitude ->
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.sendSOS(
                                            SOSRequest(
                                                deviceId = deviceId,
                                                latitude = latitude,
                                                longitude = longitude,
                                                message = "🚨 Emergency! Please Help."
                                            )
                                        )

                                        loading = false

                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "✅ Emergency Alert Sent Successfully!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "❌ Failed to dispatch SOS (${response.code()})",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        loading = false
                                        Toast.makeText(
                                            context,
                                            "Connection Error: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            onFailure = { error ->
                                loading = false
                                Toast.makeText(
                                    context,
                                    "Location Error: $error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text("SEND SOS")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}