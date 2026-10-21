package com.smartshoe.app.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        context: Context,
        onSuccess: (Double, Double) -> Unit,
        onFailure: (String) -> Unit
    ) {

        val finePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (finePermission != PackageManager.PERMISSION_GRANTED) {
            onFailure("Location permission not granted")
            return
        }

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    onSuccess(
                        location.latitude,
                        location.longitude
                    )

                } else {

                    onFailure("Unable to get current location")

                }

            }
            .addOnFailureListener {

                onFailure(it.message ?: "Location error")

            }
    }
}