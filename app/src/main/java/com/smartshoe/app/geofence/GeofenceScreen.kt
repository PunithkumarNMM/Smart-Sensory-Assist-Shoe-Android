
package com.smartshoe.app.geofence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun GeofenceScreen(navController: NavController) {

    // Declare context before using it.
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preferences = remember { GeofencePreferences(context) }
    val monitor = remember { GeofenceMonitor(context) }
    val sessionManager = remember { SessionManager(context) }

    val savedZone = remember { preferences.get() }

    var latitude by remember {
        mutableStateOf(savedZone?.latitude?.toString() ?: "")
    }
    var longitude by remember {
        mutableStateOf(savedZone?.longitude?.toString() ?: "")
    }
    var radius by remember {
        mutableStateOf(savedZone?.radiusMeters?.toInt()?.toString() ?: "100")
    }

    var liveLocationLoading by remember { mutableStateOf(false) }
    var liveLocationMessage by remember { mutableStateOf("") }

    var monitoring by remember { mutableStateOf(false) }
    var outsideAlertShown by remember { mutableStateOf(false) }

    var status by remember {
        mutableStateOf(
            if (preferences.isEnabled()) {
                "Safe zone saved. Start monitoring to check the shoe."
            } else {
                "Enter the safe-zone centre and radius."
            }
        )
    }

    // Fetch the shoe's GPS coordinates from the backend.
    Button(
        onClick = {
            scope.launch {
                liveLocationLoading = true
                liveLocationMessage = ""

                try {
                    val deviceId = sessionManager.getDeviceId()

                    if (deviceId.isNullOrBlank()) {
                        liveLocationMessage =
                            "No device registered. Connect your shoe first."
                    } else {
                        val response =
                            RetrofitClient.api.getLiveLocation(deviceId)

                        val body = response.body()
                        val location = body?.location

                        if (
                            response.isSuccessful &&
                            body?.success == true &&
                            location != null &&
                            location.latitude.isFinite() &&
                            location.longitude.isFinite() &&
                            location.latitude in -90.0..90.0 &&
                            location.longitude in -180.0..180.0 &&
                            !(location.latitude == 0.0 &&
                                    location.longitude == 0.0)
                        ) {
                            latitude = location.latitude.toString()
                            longitude = location.longitude.toString()

                            liveLocationMessage =
                                "Shoe GPS loaded. Tap Save Safe Zone to save it."
                        } else {
                            liveLocationMessage =
                                "Valid shoe GPS location is unavailable."
                        }
                    }
                } catch (e: Exception) {
                    liveLocationMessage =
                        "Could not fetch shoe location. Check your connection."
                } finally {
                    liveLocationLoading = false
                }
            }
        },
        enabled = !liveLocationLoading,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (liveLocationLoading) {
                "Fetching Shoe Location..."
            } else {
                "Use Live Location"
            }
        )
    }

    if (liveLocationMessage.isNotBlank()) {
        Text(text = liveLocationMessage)
    }

    // Monitor the shoe's location while this screen is open.
    LaunchedEffect(monitoring) {
        if (!monitoring) return@LaunchedEffect

        while (isActive) {
            val deviceId = sessionManager.getDeviceId()

            if (deviceId.isNullOrBlank()) {
                status = "No shoe device ID found. Link your shoe first."
            } else {
                when (val result = monitor.checkOnce(deviceId)) {
                    is GeofenceResult.Inside -> {
                        status =
                            "Shoe is inside the safe zone. Distance: " +
                                    "${result.distanceMeters.toInt()} m"
                        outsideAlertShown = false
                    }

                    is GeofenceResult.Outside -> {
                        status =
                            "WARNING: Shoe is outside the safe zone. " +
                                    "Distance: ${result.distanceMeters.toInt()} m"

                        if (!outsideAlertShown) {
                            GeofenceNotification.show(
                                context,
                                result.distanceMeters
                            )
                            outsideAlertShown = true
                        }
                    }

                    is GeofenceResult.Error -> {
                        status = result.message
                    }
                }
            }

            delay(5_000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Shoe Safe Zone",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Use the shoe's live GPS location or enter the safe-zone centre manually."
        )

        // The live-location button is inside the layout column.
        Button(
            onClick = {
                scope.launch {
                    liveLocationLoading = true
                    liveLocationMessage = ""

                    try {
                        val deviceId = sessionManager.getDeviceId()

                        if (deviceId.isNullOrBlank()) {
                            liveLocationMessage =
                                "No device registered. Connect your shoe first."
                        } else {
                            val response =
                                RetrofitClient.api.getLiveLocation(deviceId)
                            val body = response.body()
                            val location = body?.location

                            if (
                                response.isSuccessful &&
                                body?.success == true &&
                                location != null &&
                                location.latitude.isFinite() &&
                                location.longitude.isFinite() &&
                                location.latitude in -90.0..90.0 &&
                                location.longitude in -180.0..180.0 &&
                                !(location.latitude == 0.0 &&
                                        location.longitude == 0.0)
                            ) {
                                latitude = location.latitude.toString()
                                longitude = location.longitude.toString()
                                liveLocationMessage =
                                    "Shoe GPS loaded. Save your safe zone."
                            } else {
                                liveLocationMessage =
                                    "Valid shoe GPS location is unavailable."
                            }
                        }
                    } catch (e: Exception) {
                        liveLocationMessage =
                            "Could not fetch shoe location. Check your connection."
                    } finally {
                        liveLocationLoading = false
                    }
                }
            },
            enabled = !liveLocationLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (liveLocationLoading) "Fetching Shoe Location..."
                else "Use Live Location"
            )
        }

        if (liveLocationMessage.isNotBlank()) {
            Text(text = liveLocationMessage)
        }

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Centre latitude") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Centre longitude") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = radius,
            onValueChange = { radius = it },
            label = { Text("Radius in metres (50–5000)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val lat = latitude.toDoubleOrNull()
                val lon = longitude.toDoubleOrNull()
                val r = radius.toFloatOrNull()

                if (
                    lat == null || lon == null || r == null ||
                    !lat.isFinite() || !lon.isFinite() || !r.isFinite() ||
                    lat !in -90.0..90.0 ||
                    lon !in -180.0..180.0 ||
                    r !in 50f..5000f
                ) {
                    status =
                        "Enter valid coordinates and a radius from 50 to 5000 m."
                } else {
                    preferences.save(SafeZone(lat, lon, r))
                    status =
                        "Safe zone saved. Start monitoring to check the shoe."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Safe Zone")
        }

        Button(
            onClick = {
                if (!preferences.isEnabled()) {
                    status = "Save a valid safe zone before starting."
                } else {
                    outsideAlertShown = false
                    monitoring = true
                    status = "Monitoring started. Checking shoe GPS..."
                }
            },
            enabled = !monitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Monitoring")
        }

        OutlinedButton(
            onClick = {
                monitoring = false
                status = "Monitoring stopped."
            },
            enabled = monitoring,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop Monitoring")
        }

        Text(
            text = "Status: $status",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Keep this screen open for monitoring. Background monitoring is not enabled.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
