package com.smartshoe.app.sos

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.animation.AnimatedVisibility

@Composable
fun SosScreen(
    navController: NavController
) {

    val context = navController.context
    val scope = rememberCoroutineScope()
    var loading = remember { mutableStateOf(false) }
    var showDialog = remember { mutableStateOf(false) }
    var countdown = remember { mutableIntStateOf(3) }
    var startCountdown = remember { mutableStateOf(false) }

    val pulse = rememberInfiniteTransition(label = "pulse")

    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                900,
                easing = FastOutSlowInEasing
            ),
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

    LaunchedEffect(startCountdown.value) {
        if (!startCountdown.value) return@LaunchedEffect

        countdown.intValue = 3

        while (countdown.intValue > 0) {
            delay(1000)
            countdown.intValue--
        }

        startCountdown.value = false
        showDialog.value = true
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                Toast.makeText(
                    context,
                    "Location permission is required",
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
                    onClick = {
                        if (!loading.value) {
                            startCountdown.value = true
                        }
                    }
                ) {
                    Text(
                        text = "SOS",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = startCountdown.value
            ) {
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
                        text = "${countdown.intValue}",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

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

                    Text(text = "📍 Live GPS Location", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "📲 Notify Guardians & SMS", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🚑 Save SOS History", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "🔔 Push Emergency Notification", color = Color.White)
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text("🚨 Confirm Emergency SOS")
            },
            text = {
                Text("Your live location will be shared with guardians and an emergency SMS will be sent.\n\nDo you want to continue?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        loading.value = true

                        val session = SessionManager(context)
                        val deviceId = session.getDeviceId()

                        if (deviceId.isNullOrEmpty()) {
                            loading.value = false
                            Toast.makeText(
                                context,
                                "No Device Registered",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            loading.value = false
                            locationPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            return@Button
                        }

                        LocationHelper.getCurrentLocation(
                            context,
                            onSuccess = { latitude, longitude ->
                                scope.launch {
                                    try {
                                        // 1. Send SOS via Cloud Backend (Retrofit / MongoDB)
                                        val response = RetrofitClient.api.sendSOS(
                                            SOSRequest(
                                                deviceId,
                                                latitude,
                                                longitude,
                                                "🚨 Emergency! Please Help."
                                            )
                                        )

                                        // 2. Retrieve dynamic guardian phone number from SessionManager
                                        val registeredGuardianNumber = session.getGuardianPhone()

                                        val targetNumber = if (!registeredGuardianNumber.isNullOrEmpty()) {
                                            registeredGuardianNumber
                                        } else {
                                            "+917975855460" // Updated fallback to your real guardian number
                                        }

                                        val smsMessage = "🚨 EMERGENCY! I need help. My live location: https://maps.google.com/?q=$latitude,$longitude"

                                        // 3. Trigger Direct Local SMS to the target number
                                        (context as? com.smartshoe.app.MainActivity)?.checkAndSendSos(targetNumber, smsMessage)

                                        loading.value = false

                                        val sosResponse = response.body()

                                        if (response.isSuccessful && sosResponse?.success == true) {
                                            Toast.makeText(
                                                context,
                                                "✅ SOS sent via Cloud & Local SMS!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "⚠️ Local SMS triggered, but cloud sync returned an issue.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                    } catch (e: Exception) {
                                        loading.value = false
                                        Toast.makeText(
                                            context,
                                            e.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            onFailure = { errorMsg ->
                                loading.value = false
                                Toast.makeText(
                                    context,
                                    errorMsg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                ) {
                    if (loading.value) {
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
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}