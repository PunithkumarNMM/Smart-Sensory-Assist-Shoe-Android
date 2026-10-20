package com.smartshoe.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

object LocationHelper {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        context: Context,
        intervalMillis: Long = 1000L,
        onLocationUpdate: (Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val finePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        if (
            finePermission != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure("Location permission not granted")
            return
        }

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                context
            )

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMillis
            )
                .setMinUpdateIntervalMillis(1000L)
                .setMaxUpdateDelayMillis(1000L)
                .build()

        locationCallback =
            object : LocationCallback() {

                override fun onLocationResult(
                    locationResult: LocationResult
                ) {

                    val location =
                        locationResult.lastLocation

                    if (location != null) {

                        onLocationUpdate(
                            location.latitude,
                            location.longitude
                        )

                    } else {

                        onFailure(
                            "Unable to get current location"
                        )
                    }
                }
            }

        try {

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )

        } catch (e: Exception) {

            onFailure(
                e.message ?: "Location update error"
            )
        }
    }

    fun stopLocationUpdates() {

        if (
            fusedLocationClient != null &&
            locationCallback != null
        ) {

            fusedLocationClient?.removeLocationUpdates(
                locationCallback!!
            )

        }

        locationCallback = null
        fusedLocationClient = null
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        onSuccess: (Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val finePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        if (
            finePermission != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure("Location permission not granted")
            return
        }

        val fusedClient =
            LocationServices.getFusedLocationProviderClient(
                context
            )

        fusedClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    onSuccess(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    onFailure(
                        "Unable to get current location"
                    )
                }
            }
            .addOnFailureListener {

                onFailure(
                    it.message ?: "Location error"
                )
            }
    }
}