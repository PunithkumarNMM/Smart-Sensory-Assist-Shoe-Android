
package com.example.smartsensoryassistshoe

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceActivity : AppCompatActivity() {

    private val requestCode = 501
    private val geofenceId = "SMART_SHOE_SAFE_ZONE"

    private val geofencingClient by lazy {
        LocationServices.getGeofencingClient(this)
    }

    private lateinit var latitudeInput: EditText
    private lateinit var longitudeInput: EditText
    private lateinit var radiusInput: EditText
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        layout.addView(TextView(this).apply {
            text = "Smart Safety Shoe - Geofence"
            textSize = 24f
        })

        layout.addView(TextView(this).apply {
            text = "Enter the centre of the child's safe zone."
            textSize = 16f
        })

        latitudeInput = input("Latitude (e.g. 12.9716)")
        longitudeInput = input("Longitude (e.g. 77.5946)")
        radiusInput = input("Radius in metres (e.g. 100)")

        layout.addView(latitudeInput)
        layout.addView(longitudeInput)
        layout.addView(radiusInput)

        val saveButton = Button(this).apply {
            text = "Enable Safe Zone"
            setOnClickListener { enableGeofence() }
        }
        layout.addView(saveButton)

        val removeButton = Button(this).apply {
            text = "Disable Safe Zone"
            setOnClickListener { removeGeofence() }
        }
        layout.addView(removeButton)

        statusText = TextView(this).apply {
            text = "No new safe-zone request submitted."
            textSize = 16f
        }
        layout.addView(statusText)

        setContentView(layout)
    }


    private fun input(hintText: String): EditText {
        val editText = EditText(this)
        editText.hint = hintText
        editText.setSingleLine(true)
        editText.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED

        return editText
    }


    private fun enableGeofence() {
        val lat = latitudeInput.text.toString().toDoubleOrNull()
        val lng = longitudeInput.text.toString().toDoubleOrNull()
        val radius = radiusInput.text.toString().toFloatOrNull()

        if (lat == null || lat !in -90.0..90.0 ||
            lng == null || lng !in -180.0..180.0 ||
            radius == null || radius < 50f || radius > 5000f
        ) {
            Toast.makeText(
                this,
                "Enter valid coordinates and a radius of 50–5000 m.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestCode
            )
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            statusText.text =
                "Allow location access all the time in App Settings, " +
                        "then return and enable the safe zone."
            Toast.makeText(
                this,
                "Background location permission is required for " +
                        "reliable monitoring when the app is closed.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(
                Intent(
                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    android.net.Uri.parse("package:$packageName")
                )
            )
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(lat, lng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setLoiteringDelay(10000)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(
                GeofencingRequest.INITIAL_TRIGGER_ENTER or
                        GeofencingRequest.INITIAL_TRIGGER_EXIT
            )
            .addGeofence(geofence)
            .build()

        try {
            geofencingClient.removeGeofences(geofencePendingIntent())
                .addOnCompleteListener {
                    geofencingClient.addGeofences(
                        request, geofencePendingIntent()
                    )
                        .addOnSuccessListener {
                            statusText.text =
                                "Safe zone enabled. Radius: ${radius.toInt()} m"
                            Toast.makeText(
                                this, "Safe zone enabled",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { error ->
                            statusText.text =
                                "Could not enable safe zone: ${error.message}"
                        }
                }
        } catch (e: SecurityException) {
            statusText.text =
                "Location permission missing. Check App Settings."
        }
    }

    private fun geofencePendingIntent(): PendingIntent {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)

        var flags = PendingIntent.FLAG_UPDATE_CURRENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags = flags or PendingIntent.FLAG_MUTABLE
        }

        return PendingIntent.getBroadcast(
            this, 2001, intent, flags
        )
    }

    private fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent())
            .addOnSuccessListener {
                statusText.text = "Safe zone disabled."
                Toast.makeText(
                    this, "Safe zone disabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { error ->
                statusText.text =
                    "Could not disable safe zone: ${error.message}"
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )

        if (requestCode == this.requestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableGeofence()
        } else {
            statusText.text =
                "Location permission denied. Safe zone is not enabled."
        }
    }
}
