package com.smartshoe.app.camera

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.socket.SocketManager
import io.socket.emitter.Emitter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material.icons.filled.BrokenImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController
) {

    var refresh by remember {
        mutableIntStateOf(0)
    }

    var lastUpdate by remember {
        mutableStateOf("Waiting...")
    }

    var cameraOnline by remember {
        mutableStateOf(false)
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showImage by remember {
        mutableStateOf(false)
    }

    val pulse =
        rememberInfiniteTransition(
            label = "camera"
        )

    val pulseScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.08f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "pulse"

    )

    val onlineAlpha by pulse.animateFloat(

        initialValue = 0.4f,

        targetValue = 1f,

        animationSpec = infiniteRepeatable(

            animation = tween(700),

            repeatMode = RepeatMode.Reverse

        ),

        label = "alpha"

    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        SocketManager.connect()

        scope.launch {

            try {

                val response =
                    RetrofitClient.api.getCameraStatus()

                if (response.isSuccessful) {

                    cameraOnline =
                        response.body()?.online == true
                    loading = false

                    delay(400)

                    showImage = true

                }

            } catch (e: Exception) {

                e.printStackTrace()

            }

        }

    }

    DisposableEffect(Unit) {

        val listener = Emitter.Listener {

            refresh++

            lastUpdate =
                SimpleDateFormat(
                    "hh:mm:ss a",
                    Locale.getDefault()
                ).format(Date())

            Log.d(
                "Camera",
                "New Frame"
            )

        }

        SocketManager.getSocket()?.on(
            "newFrame",
            listener
        )

        onDispose {

            SocketManager.getSocket()?.off(
                "newFrame",
                listener
            )

            SocketManager.disconnect()

        }

    }

    val imageUrl =
        "https://smartshoe-backend.onrender.com/api/camera/latest?t=$refresh"

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Live Camera",
                        fontWeight = FontWeight.Bold
                    )

                }

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F7FC))

        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(

                text = "Camera Monitoring",

                modifier = Modifier.padding(start = 18.dp),

                fontSize = 24.sp,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        if (cameraOnline)
                            Color(0xFFE8F5E9)
                        else
                            Color(0xFFFFEBEE)
                )

            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Box(

                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                if (cameraOnline)
                                    Color(0xFF4CAF50)
                                else
                                    Color.Red
                            )

                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(

                        text =
                            if (cameraOnline)
                                "ESP32-CAM Online"
                            else
                                "ESP32-CAM Offline",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold

                    )

                }

            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(horizontal = 16.dp),

                shape = RoundedCornerShape(28.dp),

                elevation = CardDefaults.cardElevation(12.dp),

                border = BorderStroke(
                    1.dp,
                    Color.LightGray.copy(alpha = 0.4f)
                )

            ) {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (loading) {

                        Column(

                            modifier = Modifier.fillMaxSize(),

                            horizontalAlignment = Alignment.CenterHorizontally,

                            verticalArrangement = Arrangement.Center

                        ) {

                            CircularProgressIndicator()

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Text(
                                text = "Connecting to ESP32-CAM..."
                            )

                        }

                    }
                    androidx.compose.animation.AnimatedVisibility(

                        visible = showImage,

                        enter = fadeIn(
                            animationSpec = tween(700)
                        ) + scaleIn(
                            animationSpec = tween(700)
                        )

                    ) {

                        AsyncImage(

                            model = imageUrl,

                            contentDescription = "ESP32 Camera Feed",

                            modifier = Modifier.fillMaxSize(),

                            contentScale = ContentScale.Crop,

                            onSuccess = {

                                loading = false

                                cameraOnline = true

                            },

                            onError = {

                                loading = false

                                cameraOnline = false

                            }

                        )

                    }

                    Card(

                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd),

                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(
                                alpha = 0.55f
                            )
                        ),

                        shape = RoundedCornerShape(16.dp)

                    ) {

                        Row(

                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Box(

                                modifier = Modifier
                                    .size(10.dp)
                                    .scale(pulseScale)
                                    .alpha(onlineAlpha)
                                    .background(

                                        if (cameraOnline)
                                            Color.Green
                                        else
                                            Color.Red,

                                        CircleShape

                                    )

                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(

                                text =
                                    if (cameraOnline)
                                        "LIVE"
                                    else
                                        "OFFLINE",

                                color = Color.White,

                                fontWeight = FontWeight.Bold

                            )

                        }

                    }

                }

            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                shape = RoundedCornerShape(18.dp)

            ) {

                Row(

                    modifier = Modifier.padding(16.dp),

                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Icon(

                        Icons.Default.AccessTime,

                        null,

                        tint = MaterialTheme.colorScheme.primary

                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        "Last Update : $lastUpdate"
                    )

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                horizontalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                FilledTonalButton(

                    modifier = Modifier.weight(1f),

                    onClick = {

                        refresh++

                    },

                    shape = RoundedCornerShape(18.dp)

                ) {

                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Refresh")

                }

                FilledTonalButton(

                    modifier = Modifier.weight(1f),

                    onClick = {

                        refresh++

                    },

                    shape = RoundedCornerShape(18.dp)

                ) {

                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Capture")

                }

                Button(

                    modifier = Modifier.weight(1f),

                    onClick = {

                        // Full screen in next step

                    },

                    shape = RoundedCornerShape(18.dp)

                ) {

                    Icon(
                        Icons.Default.Fullscreen,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Expand")

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

        }

    }

}