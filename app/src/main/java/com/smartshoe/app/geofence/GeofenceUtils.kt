package com.smartshoe.app.geofence

import java.time.LocalTime
import kotlin.math.*

object GeofenceUtils {

    fun calculateDistance(
        latitude1: Double,
        longitude1: Double,
        latitude2: Double,
        longitude2: Double
    ): Double {

        val earthRadius = 6371000.0

        val dLat =
            Math.toRadians(latitude2 - latitude1)

        val dLon =
            Math.toRadians(longitude2 - longitude1)

        val lat1 =
            Math.toRadians(latitude1)

        val lat2 =
            Math.toRadians(latitude2)

        val a =
            sin(dLat / 2).pow(2) +
                    sin(dLon / 2).pow(2) *
                    cos(lat1) *
                    cos(lat2)

        val c =
            2 * atan2(
                sqrt(a),
                sqrt(1 - a)
            )

        return earthRadius * c
    }

    fun isWithinTimeWindow(
        startTime: String,
        endTime: String
    ): Boolean {

        return try {

            val now = LocalTime.now()

            val start =
                LocalTime.parse(startTime)

            val end =
                LocalTime.parse(endTime)

            if (start <= end) {

                now >= start && now <= end

            } else {

                now >= start || now <= end

            }

        } catch (e: Exception) {

            false

        }
    }
}