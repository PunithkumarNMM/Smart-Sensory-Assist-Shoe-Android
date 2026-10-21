
package com.smartshoe.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.messaging.FirebaseMessaging
import com.smartshoe.app.navigation.AppNavigation
import com.smartshoe.app.theme.SMARTSENSORYASSISTSHOETheme

class MainActivity : ComponentActivity() {

    companion object {
        var darkMode by mutableStateOf(false)
    }

    // SOS notification details
    var emergencyLatitude by mutableStateOf<String?>(null)
        private set

    var emergencyLongitude by mutableStateOf<String?>(null)
        private set

    var openEmergency by mutableStateOf(false)
        private set

    private fun handleEmergencyIntent(intent: Intent?) {
        if (intent?.getStringExtra("screen") == "emergency") {
            emergencyLatitude = intent.getStringExtra("latitude")
            emergencyLongitude = intent.getStringExtra("longitude")
            openEmergency = true

            Log.d(
                "SOS_NOTIFICATION",
                "SOS opened. Location: $emergencyLatitude, $emergencyLongitude"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle opening the app by tapping an SOS notification
        handleEmergencyIntent(intent)

        Log.d("FCM_TOKEN", "MainActivity onCreate started")

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_TOKEN", "FCM token retrieved successfully")
                } else {
                    Log.e(
                        "FCM_TOKEN",
                        "Failed to get FCM token",
                        task.exception
                    )
                }
            }

        enableEdgeToEdge()

        setContent {
            SMARTSENSORYASSISTSHOETheme(
                darkTheme = darkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmergencyIntent(intent)
    }
}
