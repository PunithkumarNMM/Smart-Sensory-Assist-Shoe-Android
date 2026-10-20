package com.smartshoe.app.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smartshoe.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {

    val logoScale = remember { Animatable(0.5f) }
    val logoAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(
        label = "Floating"
    )

    val floating by infiniteTransition.animateFloat(

        initialValue = 0.98f,

        targetValue = 1.03f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                1200,
                easing = FastOutSlowInEasing
            ),

            repeatMode = RepeatMode.Reverse

        ),

        label = "Float"

    )

    LaunchedEffect(Unit) {

        logoAlpha.animateTo(
            1f,
            animationSpec = tween(900)
        )

        logoScale.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
        delay(2500)

        navController.navigate("onboarding") {

            popUpTo("splash") {
                inclusive = true
            }

        }

    }

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(

                Brush.verticalGradient(

                    listOf(

                        Color(0xFF0D47A1),

                        Color(0xFF1976D2),

                        Color(0xFF42A5F5)

                    )

                )

            ),

        contentAlignment = Alignment.Center

    ) {

        Column(

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Box(

                modifier = Modifier
                    .size(160.dp)
                    .background(
                        Color.White,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center

            ) {

                Image(

                    painter = painterResource(R.drawable.app_logo),

                    contentDescription = "Logo",

                    modifier = Modifier
                        .size(130.dp)
                        .alpha(logoAlpha.value)
                        .scale(
                            logoScale.value * floating
                        )

                )

            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(

                text = "Smart Sensory",

                color = Color.White,

                fontSize = 30.sp,

                fontWeight = FontWeight.Bold

            )

            Text(

                text = "Assist Shoe",

                color = Color.White,

                fontSize = 24.sp,

                fontWeight = FontWeight.Medium

            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(

                text = "AI Powered Safety System",

                color = Color.White.copy(alpha = 0.85f),

                fontSize = 15.sp

            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(

                color = Color.White,

                strokeWidth = 4.dp

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(

                text = "Version 1.0",

                color = Color.White.copy(alpha = 0.7f),

                fontSize = 12.sp

            )

        }

    }

}

private fun OvershootInterpolator(tension: Float): Easing {

    return Easing { fraction ->

        val t = fraction - 1.0f

        t * t * ((tension + 1) * t + tension) + 1.0f

    }

}