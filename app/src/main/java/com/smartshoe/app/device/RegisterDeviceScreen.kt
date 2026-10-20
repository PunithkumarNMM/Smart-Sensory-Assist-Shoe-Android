package com.smartshoe.app.device

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.DeviceRegisterRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch
import com.smartshoe.app.utils.SessionManager

@Composable
fun RegisterDeviceScreen(
    navController: NavController
) {

    var shoeName by remember { mutableStateOf("") }
    var esp32Mac by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Register Smart Shoe",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = shoeName,
            onValueChange = { shoeName = it },
            label = { Text("Shoe Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = esp32Mac,
            onValueChange = { esp32Mac = it },
            label = { Text("ESP32 MAC Address") },
            keyboardOptions = KeyboardOptions.Default,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                loading = true

                scope.launch {

                    try {

                        val response =
                            RetrofitClient.api.registerDevice(
                                DeviceRegisterRequest(
                                    shoeName,
                                    esp32Mac
                                )
                            )

                        loading = false

                        if (response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            val deviceId = response.body()?.device?.deviceId

                            if (!deviceId.isNullOrEmpty()) {
                                val session = SessionManager(navController.context)
                                session.saveDeviceId(deviceId)
                            }

                            Toast.makeText(
                                navController.context,
                                "Device Registered Successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()

                        } else {

                            Toast.makeText(
                                navController.context,
                                response.body()?.message ?: "Registration Failed",
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

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            if (loading)
                CircularProgressIndicator()
            else
                Text("Register Device")

        }

    }
}