package com.smartshoe.app.guardian

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.smartshoe.app.model.Guardian
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun GuardianScreen(
    navController: NavController
) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var guardians by remember {
        mutableStateOf<List<Guardian>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showCards by remember {
        mutableStateOf(false)
    }

    val pulse =
        rememberInfiniteTransition(label = "fab")

    val fabScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.08f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "fab"

    )
    LaunchedEffect(Unit) {

        scope.launch {

            try {

                val response =
                    RetrofitClient.api.getGuardians()

                loading = false

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    guardians = response.body()!!.data

                    delay(250)

                    showCards = true

                }

            } catch (e: Exception) {

                loading = false

                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_LONG
                ).show()

            }

        }

    }

    if (loading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()

        }

    } else {

        Scaffold(

            containerColor = Color(0xFFF6F9FF),

            floatingActionButton = {

                FloatingActionButton(

                    modifier = Modifier.scale(fabScale),

                    containerColor = MaterialTheme.colorScheme.primary,

                    onClick = {

                        navController.navigate("addGuardian")

                    }

                ) {

                    Icon(

                        Icons.Default.Add,

                        contentDescription = null,

                        tint = Color.White

                    )

                }

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

                    text = "👨‍👩‍👧 Guardians",

                    style = MaterialTheme.typography.headlineMedium,

                    fontWeight = FontWeight.Bold

                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(

                    text = "${guardians.size} Guardian(s) Registered",

                    color = Color.Gray

                )

                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(

                    verticalArrangement = Arrangement.spacedBy(16.dp),

                    modifier = Modifier.fillMaxSize()

                ) {

                    itemsIndexed(guardians) { index, guardian ->

                        AnimatedVisibility(

                            visible = showCards,

                            enter = fadeIn(

                                animationSpec = tween(

                                    durationMillis = 500,

                                    delayMillis = index * 100

                                )

                            ) + slideInVertically(

                                animationSpec = tween(

                                    durationMillis = 500,

                                    delayMillis = index * 100

                                ),

                                initialOffsetY = { it / 2 }

                            )

                        ) {

                            ElevatedCard(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {

                                        navController.navigate(
                                            "editGuardian/${guardian._id}"
                                        )

                                    },

                                shape = RoundedCornerShape(22.dp),

                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 8.dp
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
                                            .size(70.dp)
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

                                            Icons.Default.AccountCircle,

                                            contentDescription = null,

                                            tint = Color.White,

                                            modifier = Modifier.size(46.dp)

                                        )

                                    }

                                    Spacer(modifier = Modifier.width(18.dp))

                                    Column(

                                        modifier = Modifier.weight(1f)

                                    ) {

                                        Row(

                                            verticalAlignment = Alignment.CenterVertically

                                        ) {

                                            Text(

                                                guardian.name,

                                                style = MaterialTheme.typography.titleLarge,

                                                fontWeight = FontWeight.Bold

                                            )

                                            if (guardian.isPrimary) {

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Icon(

                                                    Icons.Default.Star,

                                                    contentDescription = null,

                                                    tint = Color(0xFFFFC107)

                                                )

                                            }

                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            "Relationship : ${guardian.relationship}"
                                        )

                                        Text(
                                            "Phone : ${guardian.phone}"
                                        )

                                        Text(
                                            "Email : ${guardian.email}"
                                        )

                                    }

                                    Column(

                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {

                                        FilledIconButton(

                                            onClick = {

                                            }

                                        ) {

                                            Icon(
                                                Icons.Default.Call,
                                                contentDescription = null
                                            )

                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        FilledIconButton(

                                            onClick = {

                                            }

                                        ) {

                                            Icon(
                                                Icons.Default.Email,
                                                contentDescription = null
                                            )

                                        }

                                    }

                                }

                            }

                        }

                    }

                }
                LazyColumn(

                    verticalArrangement = Arrangement.spacedBy(16.dp),

                    modifier = Modifier.fillMaxSize()

                ) {

                    itemsIndexed(guardians) { index, guardian ->

                        AnimatedVisibility(

                            visible = showCards,

                            enter = fadeIn(

                                animationSpec = tween(

                                    durationMillis = 500,

                                    delayMillis = index * 100

                                )

                            ) + slideInVertically(

                                animationSpec = tween(

                                    durationMillis = 500,

                                    delayMillis = index * 100

                                ),

                                initialOffsetY = { it / 2 }

                            )

                        ) {

                            ElevatedCard(

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {

                                        navController.navigate(
                                            "editGuardian/${guardian._id}"
                                        )

                                    },

                                shape = RoundedCornerShape(22.dp),

                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = 8.dp
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
                                            .size(70.dp)
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

                                            Icons.Default.AccountCircle,

                                            contentDescription = null,

                                            tint = Color.White,

                                            modifier = Modifier.size(46.dp)

                                        )

                                    }

                                    Spacer(modifier = Modifier.width(18.dp))

                                    Column(

                                        modifier = Modifier.weight(1f)

                                    ) {

                                        Row(

                                            verticalAlignment = Alignment.CenterVertically

                                        ) {

                                            Text(

                                                guardian.name,

                                                style = MaterialTheme.typography.titleLarge,

                                                fontWeight = FontWeight.Bold

                                            )

                                            if (guardian.isPrimary) {

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Icon(

                                                    Icons.Default.Star,

                                                    contentDescription = null,

                                                    tint = Color(0xFFFFC107)

                                                )

                                            }

                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            "Relationship : ${guardian.relationship}"
                                        )

                                        Text(
                                            "Phone : ${guardian.phone}"
                                        )

                                        Text(
                                            "Email : ${guardian.email}"
                                        )

                                    }

                                    Column(

                                        horizontalAlignment = Alignment.CenterHorizontally

                                    ) {

                                        FilledIconButton(

                                            onClick = {

                                            }

                                        ) {

                                            Icon(
                                                Icons.Default.Call,
                                                contentDescription = null
                                            )

                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        FilledIconButton(

                                            onClick = {

                                            }

                                        ) {

                                            Icon(
                                                Icons.Default.Email,
                                                contentDescription = null
                                            )

                                        }

                                    }

                                }

                            }

                        }

                    }

                }
            }
        }
    }
}