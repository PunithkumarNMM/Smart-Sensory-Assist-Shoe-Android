package com.smartshoe.app.geofence

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.smartshoe.app.location.LocationHelper
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.delay
import com.smartshoe.app.GuardianEmergency

@Composable
fun GeofenceScreen() {

    val context = LocalContext.current

    val repository = remember {
        GeofenceRepository()
    }

    var currentLatitude by remember {
        mutableStateOf(0.0)
    }

    var currentLongitude by remember {
        mutableStateOf(0.0)
    }

    var geofences by remember {
        mutableStateOf<List<Geofence>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var locationError by remember {
        mutableStateOf("")
    }

    var guardianPhone by remember {
        mutableStateOf<String?>(null)
    }

    var alertedGeofences by remember {
        mutableStateOf(setOf<String>())
    }

    // ==========================
    // 10 SECOND VERIFICATION
    // ==========================

    var verifyingGeofences by remember {
        mutableStateOf(setOf<String>())
    }

    var verificationSeconds by remember {
        mutableStateOf<Map<String, Int>>(emptyMap())
    }

    // ==========================
    // LOCATION PERMISSION
    // ==========================

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                LocationHelper.startLocationUpdates(

                    context = context,

                    intervalMillis = 1000L,

                    onLocationUpdate = { latitude, longitude ->

                        currentLatitude = latitude
                        currentLongitude = longitude
                        locationError = ""

                    },

                    onFailure = { error ->

                        locationError = error

                    }
                )

            } else {

                locationError =
                    "Location permission denied"

                Toast.makeText(
                    context,
                    "Location permission is required for Geofencing",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // ==========================
    // LOAD GEOFENCES
    // ==========================

    LaunchedEffect(Unit) {

        try {

            geofences =
                repository.getGeofences()

        } catch (e: Exception) {

            e.printStackTrace()

        }

        isLoading = false
    }

    // ==========================
    // LOAD PRIMARY GUARDIAN
    // ==========================

    LaunchedEffect(Unit) {

        try {

            val response =
                RetrofitClient.api.getGuardians()

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val guardians =
                    response.body()?.data.orEmpty()

                val primaryGuardian =
                    guardians.firstOrNull {
                        it.isPrimary
                    } ?: guardians.firstOrNull()

                guardianPhone =
                    primaryGuardian?.phone
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    // ==========================
    // START 1 SECOND GPS UPDATES
    // ==========================

    DisposableEffect(Unit) {

        val permissionGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {

            LocationHelper.startLocationUpdates(

                context = context,

                intervalMillis = 1000L,

                onLocationUpdate = { latitude, longitude ->

                    currentLatitude = latitude
                    currentLongitude = longitude
                    locationError = ""

                },

                onFailure = { error ->

                    locationError = error

                }
            )

        } else {

            locationPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        onDispose {

            LocationHelper.stopLocationUpdates()

        }
    }

    // ==========================
    // UI
    // ==========================

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        Text(
            text = "Adaptive Geofencing"
        )

        if (isLoading) {

            CircularProgressIndicator()

        } else if (geofences.isEmpty()) {

            Text(
                text = "No geofences found"
            )

        } else {

            geofences.forEach { geofence ->

                val hasGpsLocation =
                    currentLatitude != 0.0 &&
                            currentLongitude != 0.0

                val distance =

                    if (hasGpsLocation) {

                        GeofenceUtils.calculateDistance(

                            latitude1 =
                                currentLatitude,

                            longitude1 =
                                currentLongitude,

                            latitude2 =
                                geofence.latitude,

                            longitude2 =
                                geofence.longitude
                        )

                    } else {

                        0.0
                    }

                // ==========================
                // TIME WINDOW
                // ==========================

                val withinTimeWindow =
                    GeofenceUtils.isWithinTimeWindow(

                        startTime =
                            geofence.startTime,

                        endTime =
                            geofence.endTime
                    )

                // ==========================
                // GEOFENCE STATUS
                // ==========================

                val geofenceStatus =

                    when {

                        !geofence.enabled ->
                            "DISABLED"

                        !withinTimeWindow ->
                            "INACTIVE TIME"

                        !hasGpsLocation ->
                            "WAITING FOR GPS"

                        distance <= geofence.radius ->
                            "INSIDE"

                        else ->
                            "OUTSIDE"
                    }

                // ==========================
                // 10 SECOND VERIFICATION
                // ==========================

                LaunchedEffect(
                    geofence._id,
                    geofenceStatus
                ) {

                    if (
                        geofenceStatus == "OUTSIDE" &&
                        !alertedGeofences.contains(
                            geofence._id
                        ) &&
                        !verifyingGeofences.contains(
                            geofence._id
                        )
                    ) {

                        // Start verification

                        verifyingGeofences =
                            verifyingGeofences +
                                    geofence._id

                        verificationSeconds =
                            verificationSeconds +
                                    (
                                            geofence._id to 10
                                            )

                        var returnedInside =
                            false

                        for (
                        second in 10 downTo 1
                        ) {

                            delay(1000L)

                            val latestDistance =
                                if (
                                    currentLatitude != 0.0 &&
                                    currentLongitude != 0.0
                                ) {

                                    GeofenceUtils.calculateDistance(

                                        latitude1 =
                                            currentLatitude,

                                        longitude1 =
                                            currentLongitude,

                                        latitude2 =
                                            geofence.latitude,

                                        longitude2 =
                                            geofence.longitude

                                    )

                                } else {

                                    Double.MAX_VALUE
                                }

                            val currentlyInside =
                                geofence.enabled &&
                                        GeofenceUtils
                                            .isWithinTimeWindow(
                                                geofence.startTime,
                                                geofence.endTime
                                            ) &&
                                        latestDistance <=
                                        geofence.radius

                            if (currentlyInside) {

                                returnedInside =
                                    true

                                break
                            }

                            verificationSeconds =
                                verificationSeconds +
                                        (
                                                geofence._id to
                                                        (second - 1)
                                                )
                        }

                        // ==========================
                        // CANCEL IF CHILD RETURNED
                        // ==========================

                        if (returnedInside) {

                            verifyingGeofences =
                                verifyingGeofences -
                                        geofence._id

                            verificationSeconds =
                                verificationSeconds -
                                        geofence._id

                            Toast.makeText(

                                context,

                                "Child returned inside ${geofence.name}. Alert cancelled.",

                                Toast.LENGTH_SHORT

                            ).show()

                        } else {

                            // ==========================
                            // CHILD STAYED OUTSIDE
                            // ==========================

                            verifyingGeofences =
                                verifyingGeofences -
                                        geofence._id

                            verificationSeconds =
                                verificationSeconds -
                                        geofence._id

                            if (
                                !alertedGeofences
                                    .contains(
                                        geofence._id
                                    )
                            ) {

                                alertedGeofences =
                                    alertedGeofences +
                                            geofence._id

                                // ==========================
                                // WHATSAPP ALERT
                                // ==========================

                                if (
                                    !guardianPhone
                                        .isNullOrEmpty()
                                ) {

                                    try {

                                        GuardianEmergency
                                            .sendWhatsAppMessage(

                                                context =
                                                    context,

                                                guardianPhone =
                                                    guardianPhone!!,

                                                childName =
                                                    "Your child",

                                                emergencyType =
                                                    "GEOFENCE EXIT ALERT",

                                                latitude =
                                                    currentLatitude,

                                                longitude =
                                                    currentLongitude
                                            )

                                        Toast.makeText(

                                            context,

                                            "⚠️ Geofence alert sent to parent WhatsApp",

                                            Toast.LENGTH_LONG

                                        ).show()

                                    } catch (
                                        e: Exception
                                    ) {

                                        e.printStackTrace()

                                        Toast.makeText(

                                            context,

                                            "WhatsApp alert failed",

                                            Toast.LENGTH_LONG

                                        ).show()
                                    }

                                } else {

                                    Toast.makeText(

                                        context,

                                        "⚠️ Parent WhatsApp unavailable",

                                        Toast.LENGTH_LONG

                                    ).show()
                                }
                            }
                        }
                    }

                    // ==========================
                    // CHILD IS INSIDE
                    // ==========================

                    if (
                        geofenceStatus == "INSIDE"
                    ) {

                        alertedGeofences =
                            alertedGeofences -
                                    geofence._id
                    }
                }

                // ==========================
                // GEOFENCE INFORMATION
                // ==========================

                Text(

                    text = """

                        Name: ${geofence.name}

                        Location: ${geofence.latitude}, ${geofence.longitude}

                        Radius: ${geofence.radius} m

                        Time: ${geofence.startTime} - ${geofence.endTime}

                        Enabled: ${geofence.enabled}

                    """.trimIndent()
                )

                // ==========================
                // PHONE GPS
                // ==========================

                Text(

                    text =

                        if (hasGpsLocation) {

                            "Current Phone GPS: %.6f, %.6f"
                                .format(
                                    currentLatitude,
                                    currentLongitude
                                )

                        } else {

                            "Current Phone GPS: Waiting..."
                        }
                )

                // ==========================
                // LOCATION ERROR
                // ==========================

                if (
                    locationError.isNotEmpty()
                ) {

                    Text(
                        text =
                            "Location Error: $locationError"
                    )
                }

                // ==========================
                // DISTANCE + STATUS
                // ==========================

                Text(

                    text =

                        if (hasGpsLocation) {

                            "Distance: %.1f m | Status: %s"
                                .format(
                                    distance,
                                    geofenceStatus
                                )

                        } else {

                            "Distance: -- | Status: WAITING FOR GPS"
                        }
                )

                // ==========================
                // VERIFICATION STATUS
                // ==========================

                if (
                    verifyingGeofences.contains(
                        geofence._id
                    )
                ) {

                    val secondsLeft =
                        verificationSeconds[
                            geofence._id
                        ] ?: 10

                    Text(
                        text =
                            "⚠️ Outside verification: ${secondsLeft}s remaining"
                    )
                }

                // ==========================
                // GUARDIAN STATUS
                // ==========================

                Text(

                    text =

                        if (
                            guardianPhone
                                .isNullOrEmpty()
                        ) {

                            "Guardian: Not available"

                        } else {

                            "Guardian: Connected"
                        }
                )
            }
        }
    }
}