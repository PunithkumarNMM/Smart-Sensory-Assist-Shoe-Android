package com.smartshoe.app.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.LoginRequest
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.smartshoe.app.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    navController: NavController
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }

    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        logoAlpha.animateTo(
            1f,
            animationSpec = tween(700)
        )

        logoScale.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing
            )
        )

        delay(250)

        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1565C0),
                        Color(0xFF42A5F5)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Smart Sensory",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Assist Shoe",
                fontSize = 22.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Welcome Back",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                "Sign in to continue",
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Spacer(modifier = Modifier.height(30.dp))
            AnimatedVisibility(

                visible = showContent,

                enter = fadeIn(
                    animationSpec = tween(700)
                ) + slideInVertically(
                    animationSpec = tween(700),
                    initialOffsetY = { it / 2 }
                )

            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.96f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.4f)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                            },
                            label = {
                                Text("Email")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                            },
                            label = {
                                Text("Password")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation =
                                if (passwordVisible)
                                    VisualTransformation.None
                                else
                                    PasswordVisualTransformation(),
                            trailingIcon = {

                                IconButton(
                                    onClick = {
                                        passwordVisible = !passwordVisible
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            if (passwordVisible)
                                                Icons.Default.VisibilityOff
                                            else
                                                Icons.Default.Visibility,
                                        contentDescription = null
                                    )

                                }

                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                                            RetrofitClient.api.login(
                                                LoginRequest(
                                                    email,
                                                    password
                                                )
                                            )

                                        loading = false

                                        if (
                                            response.isSuccessful &&
                                            response.body()?.success == true
                                        ) {

                                            response.body()?.token?.let {

                                                SessionManager(navController.context)
                                                    .saveToken(it)

                                            }

                                            Toast.makeText(
                                                navController.context,
                                                "Login Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            navController.navigate("dashboard") {
                                                popUpTo("login") {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }

                                        } else {

                                            Toast.makeText(
                                                navController.context,
                                                response.errorBody()?.string()
                                                    ?: "Login Failed",
                                                Toast.LENGTH_LONG
                                            ).show()

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
                        ) {

                            if (loading) {

                                CircularProgressIndicator(
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Text("Login")

                            }

                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = {
                                navController.navigate("forgotPassword")
                            }
                        ) {
                            Text("Forgot Password?")
                        }

                        TextButton(
                            onClick = {
                                navController.navigate("register")
                            }
                        ) {
                            Text("Create Account")
                        }

                    }

                }

            }
        }
    }
}