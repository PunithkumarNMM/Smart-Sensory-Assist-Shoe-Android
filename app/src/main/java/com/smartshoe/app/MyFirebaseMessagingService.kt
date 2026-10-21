
package com.smartshoe.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Locale

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val latitude = message.data["latitude"].orEmpty()
        val longitude = message.data["longitude"].orEmpty()

        val mapsUrl = message.data["mapsUrl"]
            ?: if (latitude.isNotBlank() && longitude.isNotBlank()) {
                "https://www.google.com/maps?q=$latitude,$longitude"
            } else {
                ""
            }

        val title = message.notification?.title
            ?: "🚨 EMERGENCY ALERT - Smart Shoe"

        val body = message.notification?.body
            ?: "Danger detected! Tap to view live GPS location."

        // Speak locally on the Android phone when an SOS message arrives.
        speakEmergencyMessage()

        // Preserve the existing notification and Google Maps action.
        showNotification(title, body, latitude, longitude, mapsUrl)
    }

    private fun speakEmergencyMessage() {
        val spokenMessage =
            "Emergency alert. The Smart Sensory Assist Shoe has detected danger. Please check the emergency notification and GPS location."

        var textToSpeech: TextToSpeech? = null

        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val engine = textToSpeech
                if (engine != null) {
                    val languageResult = engine.setLanguage(Locale.US)

                    if (
                        languageResult != TextToSpeech.LANG_MISSING_DATA &&
                        languageResult != TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        engine.speak(
                            spokenMessage,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "SMARTSHOE_SOS_VOICE"
                        )
                    } else {
                        Log.e("SOS_TTS", "English TTS language is unavailable")
                    }
                }
            } else {
                Log.e("SOS_TTS", "Text-to-Speech initialization failed")
            }
        }

        // Release the speech engine after the message finishes.
        // Keep the engine referenced until its initialization callback runs.
        textToSpeech?.setOnUtteranceProgressListener(
            object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    textToSpeech?.shutdown()
                }

                @Deprecated("Deprecated by Android")
                override fun onError(utteranceId: String?) {
                    textToSpeech?.shutdown()
                }
            }
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM token refreshed")
        // Register the refreshed token with your backend here.
    }

    private fun showNotification(
        title: String,
        body: String,
        latitude: String,
        longitude: String,
        mapsUrl: String
    ) {
        val channelId = "smartshoe_emergency_alerts"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "SOS and emergency notifications"
            }

            manager.createNotificationChannel(channel)
        }

        val appIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra("screen", "emergency")
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("mapsUrl", mapsUrl)
        }

        val appPendingIntent = PendingIntent.getActivity(
            this,
            1001,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val mapsPendingIntent = if (mapsUrl.isNotBlank()) {
            val mapsIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(mapsUrl)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            PendingIntent.getActivity(
                this,
                1002,
                mapsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        if (mapsUrl.isNotBlank()) {
                            "$body\n\nGPS: $latitude, $longitude"
                        } else {
                            "$body\nGPS location unavailable"
                        }
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(appPendingIntent)
                .setAutoCancel(true)

        if (mapsPendingIntent != null) {
            notificationBuilder.addAction(
                0,
                "Open Google Maps",
                mapsPendingIntent
            )
        }

        if (
            Build.VERSION.SDK_INT < 33 ||
            checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                1001,
                notificationBuilder.build()
            )
        } else {
            Log.e("FCM", "Notification permission has not been granted")
        }
    }
}
