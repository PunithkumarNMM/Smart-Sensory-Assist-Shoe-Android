package com.smartshoe.app.device


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import org.json.JSONObject
import com.smartshoe.app.models.DeviceRegisterRequest
import com.smartshoe.app.utils.SessionManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDeviceScreen(navController: NavController) {
    var shoeName by rememberSaveable { mutableStateOf("Smart Shoe") }
    var esp32Mac by rememberSaveable { mutableStateOf("") }
    var loading by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf("") }
    var isError by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Register Device") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = "Smart Shoe",
                        modifier = Modifier.height(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Connect Your Smart Shoe",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = shoeName,
                        onValueChange = { shoeName = it },
                        label = { Text("Shoe Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !loading
                    )

                    OutlinedTextField(
                        value = esp32Mac,
                        onValueChange = { esp32Mac = it },
                        label = { Text("ESP32 MAC Address") },
                        placeholder = { Text("AA:BB:CC:DD:EE:FF") },
                        supportingText = {
                            Text("Enter the MAC address of your ESP32.")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !loading
                    )

                    if (message.isNotBlank()) {
                        Text(
                            text = message,
                            color = if (isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

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
                                    isError = true
                                }

                                !macPattern.matches(mac) -> {
                                    message =
                                        "Enter a valid MAC address, e.g. AA:BB:CC:DD:EE:FF."
                                    isError = true
                                }

                                else -> {
                                    scope.launch {
                                        loading = true
                                        message = ""

                                        try {
                                            val response =
                                                RetrofitClient.api.registerDevice(
                                                    DeviceRegisterRequest(
                                                        shoeName = name,
                                                        esp32Mac = mac
                                                    )
                                                )

                                            val body = response.body()

                                            if (response.isSuccessful && body?.success == true) {
                                                val generatedId = body.device?.deviceId.orEmpty()
                                                if (generatedId.isNotBlank()) {
                                                    SessionManager(navController.context).saveDeviceId(generatedId)
                                                }

                                                message = if (generatedId.isNotBlank()) {
                                                    "Device registered successfully! ID: $generatedId"
                                                } else {
                                                    "Device registered successfully."
                                                }

                                                isError = false
                                            } else {
                                                val errorText = response.errorBody()?.string()

                                                val errorMessage = try {
                                                    JSONObject(errorText ?: "")
                                                        .optString(
                                                            "message",
                                                            "Registration failed (${response.code()})."
                                                        )
                                                } catch (_: Exception) {
                                                    "Registration failed (${response.code()})."
                                                }

                                                message = errorMessage
                                                isError = true
                                            }
                                        } catch (e: Exception) {
                                            message =
                                                "Request failed: ${e.localizedMessage ?: "Check your connection."}"
                                            isError = true
                                        } finally {
                                            loading = false
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Register Device")
                        }
                    }
                }
            }
        }
    }
}
