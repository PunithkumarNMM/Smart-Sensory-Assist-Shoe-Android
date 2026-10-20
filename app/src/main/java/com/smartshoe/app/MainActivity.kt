package com.smartshoe.app

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.smartshoe.app.navigation.AppNavigation
import com.smartshoe.app.theme.SMARTSENSORYASSISTSHOETheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {

    companion object {
        var darkMode by mutableStateOf(false)
        private const val PERMISSION_REQUEST_CODE = 101
        private const val ACTION_SMS_SENT = "com.smartshoe.app.SMS_SENT_ACTION"
    }

    private var pendingSmsNumber: String? = null
    private var pendingSmsMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safely try initializing FCM token so startup never crashes if Firebase configuration is pending
        try {
            initializeFcmToken()
        } catch (e: Exception) {
            Log.e("FCM_INIT", "Firebase initialization deferred: ${e.message}")
        }

        setContent {
            SMARTSENSORYASSISTSHOETheme(
                darkTheme = darkMode
            ) {
                AppNavigation()
            }
        }
    }

    private fun initializeFcmToken() {
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FCM_INIT", "Firebase Cloud Messaging Token: $token")
                } else {
                    Log.w("FCM_INIT", "Fetching FCM registration token failed", task.exception)
                }
            }
        } catch (e: Exception) {
            Log.e("FCM_INIT", "Firebase instance unavailable: ${e.message}")
        }
    }

    /**
     * Syncs the phone's FCM push token and shoe Device ID to MongoDB
     */
    fun syncDeviceToBackend(authToken: String, deviceIdInput: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Fetch the unique FCM token from Firebase safely
                val fcmToken = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                Log.d("SYNC_DEBUG", "Fetched FCM Token for sync: $fcmToken")

                // 2. Setup HTTP PUT request to your backend auth route
                val url = URL("https://your-backend-url.onrender.com/api/auth/update-device") // Replace with your live Render URL or localhost
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.setRequestProperty("Authorization", "Bearer $authToken")
                conn.doOutput = true

                val jsonBody = """
                    {
                        "deviceId": "$deviceIdInput",
                        "fcmToken": "$fcmToken"
                    }
                """.trimIndent()

                conn.outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("SYNC_SUCCESS", "Device ID and FCM Token successfully saved in MongoDB!")
                } else {
                    Log.e("SYNC_ERROR", "Server rejected sync with response code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("SYNC_EXCEPTION", "Failed to sync device info: ${e.message}")
            }
        }
    }

    /**
     * Trigger backend SOS API (Fast2SMS + FCM Screen Pop-up)
     */
    fun triggerBackendSos(deviceId: String, latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://your-backend-url.onrender.com/api/sos/send") // Replace with your live Render URL
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conn.doOutput = true

                val jsonBody = """
                    {
                        "deviceId": "$deviceId",
                        "latitude": $latitude,
                        "longitude": $longitude,
                        "message": "Emergency SOS triggered from Smart Shoe!"
                    }
                """.trimIndent()

                conn.outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("BACKEND_SOS", "Successfully triggered server-side SOS alert!")
                } else {
                    Log.e("BACKEND_SOS", "Server responded with error code: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("BACKEND_SOS", "Failed to reach backend SOS endpoint: ${e.message}")
            }
        }
    }

    /**
     * Called directly when the shoe or SOS button triggers an alert.
     */
    fun checkAndSendSos(phoneNumber: String, message: String) {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            pendingSmsNumber = phoneNumber
            pendingSmsMessage = message
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            sendSmsMessage(phoneNumber, message)
        }
    }

    private fun sendSmsMessage(phoneNumber: String, message: String) {
        try {
            var formattedNumber = phoneNumber.trim()
            if (!formattedNumber.startsWith("+")) {
                formattedNumber =
                    if (formattedNumber.length == 10) "+91$formattedNumber" else "+$formattedNumber"
            }

            val subId = SubscriptionManager.getDefaultSmsSubscriptionId()

            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val systemSmsManager = this.getSystemService(SmsManager::class.java)
                if (subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    systemSmsManager.createForSubscriptionId(subId)
                } else {
                    systemSmsManager
                }
            } else {
                @Suppress("DEPRECATION")
                if (subId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                    SmsManager.getSmsManagerForSubscriptionId(subId)
                } else {
                    SmsManager.getDefault()
                }
            }

            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val sentIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_SMS_SENT), flags)

            val receiverFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.RECEIVER_EXPORTED
            } else {
                0
            }

            val smsReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            Log.d(
                                "SOS_DEBUG",
                                "SUCCESS: Direct SMS delivered to cellular network for $formattedNumber"
                            )
                            Toast.makeText(
                                this@MainActivity,
                                "🚨 Emergency SMS sent to Guardian!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                            Log.e("SOS_DEBUG", "CARRIER ERROR: Generic failure")
                            Toast.makeText(
                                this@MainActivity,
                                "SMS Failed: Carrier rejected request",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    try {
                        unregisterReceiver(this)
                    } catch (_: Exception) {
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(smsReceiver, IntentFilter(ACTION_SMS_SENT), receiverFlags)
            } else {
                registerReceiver(smsReceiver, IntentFilter(ACTION_SMS_SENT))
            }

            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                val sentIntents = ArrayList<PendingIntent>()
                for (i in parts.indices) {
                    sentIntents.add(sentIntent)
                }
                smsManager.sendMultipartTextMessage(formattedNumber, null, parts, sentIntents, null)
            } else {
                smsManager.sendTextMessage(formattedNumber, null, message, sentIntent, null)
            }

            Log.d("SOS_DEBUG", "Direct SMS dispatch requested to: $formattedNumber")

        } catch (e: Exception) {
            Log.e("SOS_DEBUG", "Exception during sendSmsMessage: ${e.message}")
            Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }

            if (allGranted) {
                val number = pendingSmsNumber
                val message = pendingSmsMessage

                if (!number.isNullOrEmpty() && !message.isNullOrEmpty()) {
                    sendSmsMessage(number, message)
                    pendingSmsNumber = null
                    pendingSmsMessage = null
                }
            } else {
                Toast.makeText(this, "SMS Permissions Denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}