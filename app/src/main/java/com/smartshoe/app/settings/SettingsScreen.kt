package com.smartshoe.app.settings

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.MainActivity
import com.smartshoe.app.utils.SessionManager

data class SettingItem(

    val title: String,

    val icon: ImageVector

)

@Composable
fun SettingsScreen(

    navController: NavController

) {

    var darkMode by remember {

        mutableStateOf(
            MainActivity.darkMode
        )

    }

    var showCards by remember {

        mutableStateOf(false)

    }

    val pulse =
        rememberInfiniteTransition(
            label = "settings"
        )

    val switchScale by pulse.animateFloat(

        initialValue = 1f,

        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(

            animation = tween(900),

            repeatMode = RepeatMode.Reverse

        ),

        label = "switch"

    )

    LaunchedEffect(Unit) {

        showCards = true

    }

    val settings = listOf(

        SettingItem(
            "Dark Mode",
            Icons.Default.DarkMode
        ),

        SettingItem(
            "Change Password",
            Icons.Default.Lock
        ),

        SettingItem(
            "Privacy Policy",
            Icons.Default.Security
        ),

        SettingItem(
            "About App",
            Icons.Default.Info
        ),

        SettingItem(
            "Logout",
            Icons.Default.Logout
        )

    )
    LazyColumn(

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

            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        item {

            Text(

                text = "⚙️ Settings",

                style = MaterialTheme.typography.headlineMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                text = "Customize your Smart Sensory Assist Shoe",

                color = Color.Gray

            )

            Spacer(modifier = Modifier.height(22.dp))

        }
        items(
            count = settings.size
        ) { index ->

            val item = settings[index]

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

                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            when (item.title) {

                                "Change Password" -> {

                                    navController.navigate(
                                        "changePassword"
                                    )

                                }

                                "Privacy Policy" -> {

                                }

                                "About App" -> {

                                }

                                "Logout" -> {

                                    SessionManager(navController.context)
                                        .clearSession()

                                    navController.navigate("login") {

                                        popUpTo(0)

                                    }

                                }

                            }

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
                                .size(58.dp)
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

                                imageVector = item.icon,

                                contentDescription = null,

                                tint = Color.White,

                                modifier = Modifier.size(28.dp)

                            )

                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Text(

                            text = item.title,

                            modifier = Modifier.weight(1f),

                            style = MaterialTheme.typography.titleMedium,

                            fontWeight = FontWeight.Bold

                        )

                        if (item.title == "Dark Mode") {

                            Switch(

                                modifier = Modifier.scale(switchScale),

                                checked = darkMode,

                                onCheckedChange = {

                                    darkMode = it

                                    MainActivity.darkMode = it

                                }

                            )

                        } else {

                            Icon(

                                Icons.Default.KeyboardArrowRight,

                                contentDescription = null,

                                tint = Color.Gray

                            )

                        }

                    }

                }

            }

        }
        item {

            Spacer(modifier = Modifier.height(32.dp))

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(22.dp),

                colors = CardDefaults.cardColors(

                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)

                )

            ) {

                Column(

                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    Icon(

                        imageVector = Icons.Default.Settings,

                        contentDescription = null,

                        modifier = Modifier.size(52.dp),

                        tint = MaterialTheme.colorScheme.primary

                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(

                        text = "Smart Sensory Assist Shoe",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(

                        text = "Version 1.0.0",

                        color = Color.Gray

                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(

                        text = "Powered by IoT & AI",

                        color = MaterialTheme.colorScheme.primary,

                        fontWeight = FontWeight.SemiBold

                    )

                }

            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}