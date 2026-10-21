
package com.smartshoe.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartshoe.app.models.DeviceRegisterRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RegisterDeviceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterDeviceScreen(
                        onRegisterSuccess = {
                            Toast.makeText(
                                this,
                                "Device Registered Successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        },
                        onError = { message ->
                            Toast.makeText(
                                this,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterDeviceScreen(
    onRegisterSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    var shoeName by remember { mutableStateOf("Smart Shoe") }
    var esp32Mac by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register Smart Shoe",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = shoeName,
            onValueChange = { shoeName = it },
            label = { Text("Shoe Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = esp32Mac,
            onValueChange = { esp32Mac = it },
            label = { Text("ESP32 MAC Address") },
            placeholder = { Text("AA:BB:CC:DD:EE:FF") },
            supportingText = {
                Text("Enter your ESP32's actual MAC address.")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = if (message.startsWith("Success")) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val name = shoeName.trim()
                val mac = esp32Mac.trim()

                val macPattern =
                    Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")

                when {
                    name.length !in 3..50 -> {
                        message =
                            "Shoe name must be between 3 and 50 characters."
                    }

                    !macPattern.matches(mac) -> {
                        message =
                            "Enter a valid MAC address, e.g. AA:BB:CC:DD:EE:FF."
                    }

                    else -> {
                        scope.launch {
                            isLoading = true
                            message = ""

                            try {
                                val response = withContext(Dispatchers.IO) {
                                    RetrofitClient.api.registerDevice(
                                        DeviceRegisterRequest(
                                            shoeName = name,
                                            esp32Mac = mac
                                        )
                                    )
                                }

                                if (response.isSuccessful) {
                                    val body = response.body()

                                    if (body?.success == true) {
                                        message =
                                            "Success: Device registered."
                                        onRegisterSuccess()
                                    } else {
                                        message =
                                            body?.message
                                                ?: "Registration response was unsuccessful."
                                        onError(message)
                                    }
                                } else {
                                    val errorText =
                                        response.errorBody()?.string()

                                    val serverMessage = try {
                                        JSONObject(errorText ?: "")
                                            .optString(
                                                "message",
                                                "Registration failed (${response.code()})."
                                            )
                                    } catch (_: Exception) {
                                        "Registration failed (${response.code()})."
                                    }

                                    message = serverMessage
                                    onError(serverMessage)
                                }
                            } catch (e: Exception) {
                                val error =
                                    "Connection error: ${e.localizedMessage ?: "Check your internet and server."}"
                                message = error
                                onError(error)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register Device", fontSize = 16.sp)
            }
        }
    }
}
