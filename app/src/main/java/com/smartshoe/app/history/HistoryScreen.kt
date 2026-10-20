package com.smartshoe.app.history

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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.SOSHistory
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.smartshoe.app.components.LottieLoader

@Composable
fun HistoryScreen(
    navController: NavController
) {

    val context = navController.context

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var history by remember {
        mutableStateOf<List<SOSHistory>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var search by remember {
        mutableStateOf("")
    }

    var showCards by remember {
        mutableStateOf(false)
    }

    val pulse =
        rememberInfiniteTransition(label = "history")

    val searchScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "search"

    )
    LaunchedEffect(Unit) {

        scope.launch {

            try {

                val deviceId =
                    SessionManager(context)
                        .getDeviceId()

                if (deviceId.isNullOrEmpty()) {

                    loading = false
                    return@launch

                }

                val response =
                    RetrofitClient.api
                        .getSOSHistory(deviceId)

                loading = false

                if (

                    response.isSuccessful &&
                    response.body()?.success == true

                ) {

                    history = response.body()!!.data

                    delay(250)

                    showCards = true

                }

            } catch (e: Exception) {

                loading = false

                scope.launch {

                    snackbarHostState.showSnackbar(

                        e.message ?: "Unknown Error"

                    )

                }

            }

        }

    }

    if (loading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            LottieLoader(
                fileName = "loading.json",
                modifier = Modifier.size(180.dp)
            )

        }

    } else {

        Scaffold(

            snackbarHost = {

                SnackbarHost(
                    hostState = snackbarHostState
                )

            }

        ) { padding ->

            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                Text(

                    text = "📜 Emergency History",

                    style = MaterialTheme.typography.headlineMedium,

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(

                    value = search,

                    onValueChange = {

                        search = it

                    },

                    leadingIcon = {

                        Icon(
                            Icons.Default.Search,
                            null
                        )

                    },

                    placeholder = {

                        Text("Search history")

                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(searchScale),

                    singleLine = true

                )

                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(

                    verticalArrangement = Arrangement.spacedBy(16.dp)

                ) {

                    items(
                        count = history.size
                    ) { index ->

                        val item = history[index]

                        AnimatedVisibility(

                            visible = showCards,

                            enter = fadeIn(

                                animationSpec = tween(

                                    500,

                                    delayMillis = index * 80

                                )

                            ) + slideInVertically(

                                animationSpec = tween(

                                    500,

                                    delayMillis = index * 80

                                ),

                                initialOffsetY = { it / 2 }

                            )

                        ) {

                            ElevatedCard(

                                modifier = Modifier.fillMaxWidth(),

                                shape = RoundedCornerShape(22.dp),

                                elevation = CardDefaults.elevatedCardElevation(8.dp)

                            ) {

                                Row(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),

                                    verticalAlignment = Alignment.Top

                                ) {

                                    Box(

                                        modifier = Modifier
                                            .size(62.dp)
                                            .background(

                                                Brush.linearGradient(

                                                    listOf(

                                                        Color(0xFFE53935),

                                                        Color(0xFFFF7043)

                                                    )

                                                ),

                                                CircleShape

                                            ),

                                        contentAlignment = Alignment.Center

                                    ) {

                                        Icon(

                                            Icons.Default.Warning,

                                            null,

                                            tint = Color.White

                                        )

                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(

                                        modifier = Modifier.weight(1f)

                                    ) {

                                        Text(

                                            "Emergency SOS",

                                            style = MaterialTheme.typography.titleMedium,

                                            fontWeight = FontWeight.Bold

                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        AssistChip(

                                            onClick = {},

                                            label = {

                                                Text("SOS")

                                            }

                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text("Latitude : ${item.latitude}")

                                        Text("Longitude : ${item.longitude}")

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(item.message)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        HorizontalDivider()

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(

                                            item.createdAt,

                                            color = Color.Gray,

                                            style = MaterialTheme.typography.bodySmall

                                        )

                                    }

                                }

                            }

                        }

                    }

                }
                if (history.isEmpty()) {

                    Box(

                        modifier = Modifier.fillMaxSize(),

                        contentAlignment = Alignment.Center

                    ) {

                        Column(

                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {

                            Icon(

                                imageVector = Icons.Default.Warning,

                                contentDescription = null,

                                modifier = Modifier.size(90.dp),

                                tint = Color.Gray

                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(

                                "No Emergency History",

                                style = MaterialTheme.typography.headlineSmall,

                                fontWeight = FontWeight.Bold

                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(

                                "Emergency alerts will appear here.",

                                color = Color.Gray

                            )

                        }

                    }

                }

            }

        }
    }
}