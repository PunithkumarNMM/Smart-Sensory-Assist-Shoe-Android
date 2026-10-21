package com.smartshoe.app.device

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager

@Composable
fun DeviceScreen(
    navController: NavController
) {

    val context = navController.context
    val session = SessionManager(context)

    var device by remember {
        mutableStateOf<DeviceData?>(null)
    }

    LaunchedEffect(Unit) {

        val deviceId = session.getDeviceId()

        if (!deviceId.isNullOrEmpty()) {

            try {

                val response =
                    RetrofitClient.api.getDeviceStatus(deviceId)

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    device = response.body()!!.data

                }

            } catch (_: Exception) {
            }

        }

    }

    if (device == null) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            CircularProgressIndicator()

        }

    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {

                Text(
                    text = "My Smart Shoe",

                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        SessionManager(navController.context).clearDevice()

                        navController.navigate("registerDevice")

                    }
                ) {

                    Text("REGISTER NEW DEVICE")

                }
            }

           item{ DeviceStatusCard("Shoe Name", device?.shoeName ?: "Not Available")}
               item{ DeviceStatusCard("Device ID", device?.deviceId ?: "Not Available")}
                   item{DeviceStatusCard("Battery", "${device?.battery ?: 0}%")}
                       item{DeviceStatusCard("Status", device?.status ?: "Offline")}
                           item{ DeviceStatusCard("Firmware", device?.firmware ?: "Not Available")}
                               item{  DeviceStatusCard("GPS", if (device?.gps == true) "Enabled" else "Disabled")}
                                   item{ DeviceStatusCard("Camera", if (device?.camera == true) "Enabled" else "Disabled")}
                                       item{ DeviceStatusCard("ESP32 MAC", device?.mac ?: "Not Available")}
                                           item{   DeviceStatusCard("Last Seen", device?.lastSeen ?: "Never")}

        }

    }

}