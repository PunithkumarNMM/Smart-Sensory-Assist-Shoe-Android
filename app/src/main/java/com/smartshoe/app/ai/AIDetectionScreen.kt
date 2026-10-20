package com.smartshoe.app.ai

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartshoe.app.model.SOSRequest
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun AIDetectionScreen(
    navController: NavController
) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var result by remember {
        mutableStateOf("AI Ready")
    }

    var confidence by remember {
        mutableStateOf(0.0)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var showCards by remember {
        mutableStateOf(false)
    }

    val pulse =
        rememberInfiniteTransition(label = "ai")

    val iconScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.08f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "icon"

    )

    LaunchedEffect(Unit) {

        showCards = true

    }
    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(

                Brush.verticalGradient(

                    listOf(

                        Color(0xFFF7F9FC),

                        Color.White

                    )

                )

            )
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        AnimatedVisibility(

            visible = showCards,

            enter = fadeIn() + scaleIn()

        ) {

            Box(

                modifier = Modifier
                    .size(140.dp)
                    .background(

                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),

                        CircleShape

                    ),

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    Icons.Default.Psychology,

                    contentDescription = null,

                    modifier = Modifier
                        .size(80.dp)
                        .scale(iconScale),

                    tint = MaterialTheme.colorScheme.primary

                )

            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(

            "🤖 AI Fall Detection",

            style = MaterialTheme.typography.headlineMedium,

            fontWeight = FontWeight.Bold

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(

            result,

            color = Color.Gray

        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp)

        ) {

            Column(

                modifier = Modifier.padding(20.dp)

            ) {

                Text(

                    "Detection Confidence",

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(

                    progress = { confidence.toFloat() },

                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(

                    "${(confidence * 100).toInt()}%"

                )

            }

        }

        Spacer(modifier = Modifier.height(28.dp))
        Button(

            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),

            shape = RoundedCornerShape(18.dp),

            enabled = !loading,

            onClick = {

                loading = true

                scope.launch {

                    try {

                        val response =

                            RetrofitClient.api.detectFall(

                                AiRequest(

                                    prediction = "fall",

                                    confidence = 0.95

                                )

                            )

                        loading = false

                        if (

                            response.isSuccessful &&

                            response.body() != null

                        ) {

                            val ai = response.body()!!

                            confidence = ai.confidence

                            if (ai.emergency) {

                                result = "🚨 FALL DETECTED"

                                SessionManager(context)

                                    .getDeviceId()

                                    ?.let { deviceId ->

                                        RetrofitClient.api.sendSOS(

                                            SOSRequest(

                                                deviceId = deviceId,

                                                latitude = 12.9716,

                                                longitude = 77.5946,

                                                message = "AI detected a fall"

                                            )

                                        )

                                    }

                            } else {

                                result = "✅ NORMAL"

                            }

                        } else {

                            result = "❌ DETECTION FAILED"

                        }

                    } catch (e: Exception) {

                        loading = false

                        result = "⚠️ ERROR"

                    }

                }

            }

        ) {

            if (loading) {

                CircularProgressIndicator(

                    modifier = Modifier.size(26.dp),

                    color = Color.White,

                    strokeWidth = 3.dp

                )

            } else {

                Icon(

                    Icons.Default.PlayArrow,

                    contentDescription = null

                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(

                    "Run AI Detection",

                    fontWeight = FontWeight.Bold

                )

            }

        }
        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(22.dp)

        ) {

            Column(

                modifier = Modifier.padding(20.dp)

            ) {

                Text(

                    text = "AI Status",

                    style = MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Text("Model")

                    Text("Fall Detection AI")

                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Text("Confidence")

                    Text("${(confidence * 100).toInt()}%")

                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(

                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween

                ) {

                    Text("Status")

                    Text(

                        result,

                        color =

                            if (result.contains("FALL"))

                                Color.Red
                            else

                                Color(0xFF2E7D32),

                        fontWeight = FontWeight.Bold

                    )

                }

            }

        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}