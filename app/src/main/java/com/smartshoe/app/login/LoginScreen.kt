
package com.smartshoe.app.login

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.smartshoe.app.R
import com.smartshoe.app.model.LoginRequest
import com.smartshoe.app.model.UpdateDeviceRequest
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }

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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome Back",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Sign in to continue",
                color = Color.White.copy(alpha = 0.8f)
            )

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
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        passwordVisible = !passwordVisible
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (passwordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = if (passwordVisible) {
                                            "Hide password"
                                        } else {
                                            "Show password"
                                        }
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
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        navController.context,
                                        "Enter your email and password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                loading = true

                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.login(
                                            LoginRequest(email, password)
                                        )

                                        if (
                                            response.isSuccessful &&
                                            response.body()?.success == true
                                        ) {
                                            val loginBody = response.body()
                                            val token = loginBody?.token

                                            if (!token.isNullOrBlank()) {
                                                SessionManager(navController.context)
                                                    .saveToken(token)
                                            } else {
                                                Log.e(
                                                    "FCM_REGISTER",
                                                    "Login response did not contain an auth token"
                                                )
                                            }

                                            loading = false

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

                                            // Retrieve FCM token and register it
                                            // with the authenticated backend.
                                            FirebaseMessaging.getInstance().token
                                                .addOnSuccessListener { fcmToken ->
                                                    scope.launch {
                                                        try {
                                                            if (fcmToken.isBlank()) {
                                                                Log.e(
                                                                    "FCM_REGISTER",
                                                                    "Firebase returned an empty FCM token"
                                                                )
                                                                return@launch
                                                            }

                                                            val session =
                                                                SessionManager(navController.context)

                                                            val deviceId =
                                                                session.getDeviceId()

                                                            if (deviceId.isNullOrBlank()) {
                                                                Log.w(
                                                                    "FCM_REGISTER",
                                                                    "No device ID saved. Register the shoe device first."
                                                                )
                                                                return@launch
                                                            }

                                                            val updateResponse =
                                                                RetrofitClient.api.updateDevice(
                                                                    UpdateDeviceRequest(
                                                                        deviceId = deviceId,
                                                                        fcmToken = fcmToken
                                                                    )
                                                                )

                                                            if (updateResponse.isSuccessful) {
                                                                Log.d(
                                                                    "FCM_REGISTER",
                                                                    "Device and FCM token registration succeeded. HTTP ${updateResponse.code()}"
                                                                )
                                                            } else {
                                                                val errorBody =
                                                                    updateResponse.errorBody()
                                                                        ?.string()

                                                                Log.e(
                                                                    "FCM_REGISTER",
                                                                    "Registration failed. HTTP ${updateResponse.code()}: ${errorBody ?: "No error details"}"
                                                                )
                                                            }
                                                        } catch (e: Exception) {
                                                            Log.e(
                                                                "FCM_REGISTER",
                                                                "Exception while registering device and FCM token",
                                                                e
                                                            )
                                                        }
                                                    }
                                                }
                                                .addOnFailureListener { error ->
                                                    Log.e(
                                                        "FCM_REGISTER",
                                                        "Failed to retrieve FCM token",
                                                        error
                                                    )
                                                }
                                        } else {
                                            loading = false

                                            Toast.makeText(
                                                navController.context,
                                                response.errorBody()?.string()
                                                    ?: "Login Failed",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        loading = false

                                        Log.e(
                                            "LOGIN",
                                            "Login request failed",
                                            e
                                        )

                                        Toast.makeText(
                                            navController.context,
                                            e.message ?: "Login failed. Please try again.",
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
