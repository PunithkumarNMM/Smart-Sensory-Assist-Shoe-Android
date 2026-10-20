package com.smartshoe.app.notification

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.smartshoe.app.model.Notification
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.socket.SocketManager
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NotificationScreen(
    navController: NavController
) {

    val scope = rememberCoroutineScope()

    var loading by remember {
        mutableStateOf(true)
    }

    var notifications by remember {
        mutableStateOf<List<Notification>>(emptyList())
    }

    var showCards by remember {
        mutableStateOf(false)
    }

    val pulse =
        rememberInfiniteTransition(
            label = "notification"
        )

    val refreshScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.08f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "refresh"

    )

    fun loadNotifications() {

        scope.launch {

            try {

                loading = true

                val response =
                    RetrofitClient.api.getNotifications()

                loading = false

                if (

                    response.isSuccessful &&

                    response.body()?.success == true

                ) {

                    notifications =
                        response.body()!!.data

                    delay(250)

                    showCards = true

                }

            } catch (e: Exception) {

                loading = false

                Toast.makeText(

                    navController.context,

                    e.message,

                    Toast.LENGTH_LONG

                ).show()

            }

        }

    }
    LaunchedEffect(Unit) {

        loadNotifications()

        SocketManager.connect()

    }

    DisposableEffect(Unit) {

        val listener = Emitter.Listener {

            loadNotifications()

        }

        SocketManager.getSocket()
            ?.on("notification:created", listener)

        onDispose {

            SocketManager.getSocket()
                ?.off("notification:created", listener)

        }

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
            .padding(16.dp)

    ) {

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically

        ) {

            Column {

                Text(

                    text = "🔔 Notifications",

                    style = MaterialTheme.typography.headlineMedium,

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(

                    text = "${notifications.size} Notifications",

                    color = Color.Gray

                )

            }

            FilledIconButton(

                modifier = Modifier.scale(refreshScale),

                onClick = {

                    loadNotifications()

                }

            ) {

                Icon(

                    Icons.Default.Refresh,

                    contentDescription = null

                )

            }

        }

        Spacer(modifier = Modifier.height(18.dp))
        LazyColumn(

            verticalArrangement = Arrangement.spacedBy(16.dp),

            modifier = Modifier.fillMaxSize()

        ) {

            items(
                count = notifications.size
            ) { index ->

                val notification = notifications[index]

                AnimatedVisibility(

                    visible = showCards,

                    enter = fadeIn(

                        animationSpec = tween(

                            durationMillis = 500,

                            delayMillis = index * 80

                        )

                    ) + slideInVertically(

                        animationSpec = tween(

                            durationMillis = 500,

                            delayMillis = index * 80

                        ),

                        initialOffsetY = { it / 2 }

                    )

                ) {

                    ElevatedCard(

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(22.dp),

                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 8.dp
                        )

                    ) {

                        Column(

                            modifier = Modifier.padding(18.dp)

                        ) {

                            Row(

                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Box(

                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(

                                            Brush.linearGradient(

                                                listOf(

                                                    Color(0xFF1976D2),

                                                    Color(0xFF64B5F6)

                                                )

                                            ),

                                            CircleShape

                                        ),

                                    contentAlignment = Alignment.Center

                                ) {

                                    Icon(

                                        Icons.Default.Notifications,

                                        contentDescription = null,

                                        tint = Color.White,

                                        modifier = Modifier.size(28.dp)

                                    )

                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(

                                    modifier = Modifier.weight(1f)

                                ) {

                                    Text(

                                        text = notification.title,

                                        style = MaterialTheme.typography.titleMedium,

                                        fontWeight = FontWeight.Bold

                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(

                                        text = notification.createdAt,

                                        style = MaterialTheme.typography.bodySmall,

                                        color = Color.Gray

                                    )

                                }

                                if (!notification.isRead) {

                                    Badge(

                                        containerColor = Color(0xFF2ECC71)

                                    ) {

                                        Text(

                                            "NEW",

                                            color = Color.White

                                        )

                                    }

                                }

                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(

                                text = notification.message,

                                style = MaterialTheme.typography.bodyMedium

                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(

                                modifier = Modifier.fillMaxWidth(),

                                horizontalArrangement = Arrangement.End

                            ) {

                                FilledIconButton(

                                    onClick = {

                                        scope.launch {

                                            RetrofitClient.api
                                                .markNotificationRead(
                                                    notification._id
                                                )

                                            loadNotifications()

                                        }

                                    }

                                ) {

                                    Icon(

                                        Icons.Default.MarkEmailRead,

                                        contentDescription = null

                                    )

                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                FilledIconButton(

                                    colors = IconButtonDefaults.filledIconButtonColors(

                                        containerColor = Color.Red

                                    ),

                                    onClick = {

                                        scope.launch {

                                            RetrofitClient.api
                                                .deleteNotification(
                                                    notification._id
                                                )

                                            loadNotifications()

                                        }

                                    }

                                ) {

                                    Icon(

                                        Icons.Default.Delete,

                                        contentDescription = null,

                                        tint = Color.White

                                    )

                                }

                            }

                        }

                    }

                }

            }

        }
    }

    if (notifications.isEmpty()) {

        Box(

            modifier = Modifier.fillMaxSize(),

            contentAlignment = Alignment.Center

        ) {

            Column(

                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                Icon(

                    imageVector = Icons.Default.Notifications,

                    contentDescription = null,

                    modifier = Modifier.size(90.dp),

                    tint = Color.Gray

                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(

                    text = "No Notifications Yet",

                    style = MaterialTheme.typography.headlineSmall,

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(

                    text = "You'll receive alerts from your Smart Shoe here.",

                    style = MaterialTheme.typography.bodyMedium,

                    color = Color.Gray

                )

                Spacer(modifier = Modifier.height(24.dp))

                FilledTonalButton(

                    onClick = {

                        loadNotifications()

                    }

                ) {

                    Icon(

                        Icons.Default.Refresh,

                        contentDescription = null

                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Refresh")

                }

            }

        }

    }

}

