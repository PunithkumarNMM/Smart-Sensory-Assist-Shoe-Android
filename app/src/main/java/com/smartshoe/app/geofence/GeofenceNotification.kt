
package com.smartshoe.app.geofence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.smartshoe.app.R

object GeofenceNotification {

    private const val CHANNEL_ID = "smart_shoe_safe_zone"
    private const val NOTIFICATION_ID = 4101

    fun show(context: Context, distanceMeters: Double) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Smart Shoe Safe Zone",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts when the shoe leaves its safe zone"
                }
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Smart Shoe: Safe Zone Alert")
            .setContentText(
                "The shoe is approximately ${distanceMeters.toInt()} m from the safe-zone centre."
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "The shoe's reported GPS position is outside your configured safe zone."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, notification)
    }
}
