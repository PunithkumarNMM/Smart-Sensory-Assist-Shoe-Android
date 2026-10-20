package com.smartshoe.app.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.Esp32Retrofit
import kotlinx.coroutines.delay


data class ActivityData(
    val steps: Int = 0,
    val activityLevel: String = "Low",
    val activityStatus: String = "Normal Activity",
    val fallDetected: Boolean = false,
    val heartRate: Int = 0
)


@Composable
fun ActivityScreen(
    navController: NavController
) {

    var activityStatus by remember {
        mutableStateOf("Normal Activity")
    }

    var steps by remember {
        mutableStateOf(0)
    }

    var activityLevel by remember {
        mutableStateOf("Low")
    }

    var fallStatus by remember {
        mutableStateOf("No Fall Detected")
    }

    var heartRate by remember {
        mutableStateOf(0)
    }

    var emergency by remember {
        mutableStateOf(false)
    }

    var fallCountdown by remember {
        mutableStateOf(0)
    }

    var childMoving by remember {
        mutableStateOf(false)
    }


    /*
     * ESP32 LIVE DATA
     */

    LaunchedEffect(Unit) {

        while (true) {

            try {

                val response =
                    Esp32Retrofit.api.getData()

                if (response.isSuccessful) {

                    val data =
                        response.body()

                    if (data != null) {

                        /*
                         * NORMAL ACTIVITY
                         */

                        if (!data.fall && !data.emergency) {

                            activityStatus =
                                "Normal Activity"

                            fallStatus =
                                "No Fall Detected"

                            emergency =
                                false

                            fallCountdown =
                                0
                        }


                        /*
                         * FALL DETECTED
                         */

                        if (data.fall && !emergency) {

                            activityStatus =
                                "Possible Fall"

                            fallStatus =
                                "Fall Detected"

                            /*
                             * Start intelligent SOS
                             */

                            if (fallCountdown == 0) {

                                fallCountdown =
                                    8
                            }
                        }


                        /*
                         * EMERGENCY FROM ESP32
                         */

                        if (data.emergency) {

                            emergency =
                                true

                            activityStatus =
                                "EMERGENCY"

                            fallStatus =
                                "Emergency Detected"

                            fallCountdown =
                                0
                        }


                        /*
                         * Activity level
                         */

                        activityLevel =
                            when {

                                data.distance < 20 ->
                                    "High"

                                data.distance < 50 ->
                                    "Medium"

                                else ->
                                    "Low"
                            }
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

            }

            delay(1000)
        }
    }


    /*
     * 8 SECOND INTELLIGENT SOS
     */

    LaunchedEffect(fallCountdown) {

        if (fallCountdown > 0 && !emergency) {

            for (
            second in fallCountdown downTo 1
            ) {

                fallCountdown =
                    second

                delay(1000)
            }


            /*
             * After 8 seconds:
             *
             * If child moved:
             * cancel SOS
             *
             * If child did not move:
             * emergency
             */

            if (childMoving) {

                fallCountdown =
                    0

                fallStatus =
                    "Fall Cancelled"

                activityStatus =
                    "Child Moving"

            } else {

                emergency =
                    true

                fallCountdown =
                    0

                fallStatus =
                    "Emergency Detected"

                activityStatus =
                    "EMERGENCY"
            }
        }
    }


    /*
     * MAIN SCREEN
     */

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF7F9FC)
                )
                .padding(18.dp)

    ) {

        Text(

            text =
                "Activity",

            style =
                MaterialTheme
                    .typography
                    .headlineLarge,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        Text(

            text =
                "Monitor the child's current activity",

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                Color.Gray
        )


        Spacer(
            modifier =
                Modifier.height(22.dp)
        )


        /*
         * EMERGENCY CARD
         */

        if (emergency) {

            EmergencyCard()

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )
        }


        /*
         * FALL COUNTDOWN
         */

        if (fallCountdown > 0 && !emergency) {

            FallCountdownCard(
                seconds =
                    fallCountdown
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )
        }


        /*
         * SUMMARY
         */

        ActivitySummaryCard(

            activityStatus =
                activityStatus,

            activityLevel =
                activityLevel,

            fallStatus =
                fallStatus
        )


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        /*
         * STEPS + ACTIVITY
         */

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.spacedBy(14.dp)

        ) {

            ActivityInfoCard(

                title =
                    "Steps",

                value =
                    steps.toString(),

                icon =
                    Icons.Default.DirectionsRun,

                modifier =
                    Modifier.weight(1f)
            )


            ActivityInfoCard(

                title =
                    "Activity",

                value =
                    activityLevel,

                icon =
                    Icons.Default.Favorite,

                modifier =
                    Modifier.weight(1f)
            )
        }


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        /*
         * FALL STATUS
         */

        ActivityInfoCard(

            title =
                "Fall Status",

            value =
                fallStatus,

            icon =
                Icons.Default.HealthAndSafety,

            modifier =
                Modifier.fillMaxWidth()
        )


        Spacer(
            modifier =
                Modifier.height(18.dp)
        )


        /*
         * HEART RATE
         */

        ActivityInfoCard(

            title =
                "Heart Rate",

            value =
                if (heartRate > 0)
                    "$heartRate BPM"
                else
                    "-- BPM",

            icon =
                Icons.Default.Favorite,

            modifier =
                Modifier.fillMaxWidth()
        )
    }
}


/*
 * EMERGENCY CARD
 */

@Composable
private fun EmergencyCard() {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFE5E5)
            ),

        elevation =
            CardDefaults.cardElevation(
                8.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(22.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Icon(

                imageVector =
                    Icons.Default.Warning,

                contentDescription =
                    null,

                tint =
                    Color.Red,

                modifier =
                    Modifier.size(50.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            Text(

                text =
                    "EMERGENCY",

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )


            Text(

                text =
                    "Possible fall confirmed",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


/*
 * FALL COUNTDOWN CARD
 */

@Composable
private fun FallCountdownCard(
    seconds: Int
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFFFFF3CD)
            ),

        elevation =
            CardDefaults.cardElevation(
                8.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(22.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Icon(

                imageVector =
                    Icons.Default.Warning,

                contentDescription =
                    null,

                tint =
                    Color(0xFFFF9800),

                modifier =
                    Modifier.size(42.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(

                text =
                    "Possible Fall Detected",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(

                text =
                    "Checking child's movement...",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            Text(

                text =
                    "$seconds",

                style =
                    MaterialTheme
                        .typography
                        .displayLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "seconds remaining",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                color =
                    Color.Gray
            )
        }
    }
}


/*
 * ACTIVITY SUMMARY CARD
 */

@Composable
private fun ActivitySummaryCard(

    activityStatus: String,

    activityLevel: String,

    fallStatus: String

) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(24.dp),

        elevation =
            CardDefaults.cardElevation(
                8.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(22.dp)

        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Box(

                    modifier =
                        Modifier
                            .size(58.dp)
                            .background(

                                MaterialTheme
                                    .colorScheme
                                    .primary
                                    .copy(
                                        alpha = 0.12f
                                    ),

                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center

                ) {

                    Icon(

                        imageVector =
                            Icons.Default.DirectionsRun,

                        contentDescription =
                            null,

                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary,

                        modifier =
                            Modifier.size(30.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.size(16.dp)
                )


                Column {

                    Text(

                        text =
                            activityStatus,

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(

                        text =
                            "Activity level: $activityLevel",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            Color.Gray
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            Text(

                text =
                    fallStatus,

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                fontWeight =
                    FontWeight.Medium
            )
        }
    }
}


/*
 * INFORMATION CARD
 */

@Composable
private fun ActivityInfoCard(

    title: String,

    value: String,

    icon: ImageVector,

    modifier: Modifier =
        Modifier

) {

    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(20.dp),

        elevation =
            CardDefaults.cardElevation(
                6.dp
            )

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    null,

                tint =
                    MaterialTheme
                        .colorScheme
                        .primary,

                modifier =
                    Modifier.size(32.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(

                text =
                    value,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(

                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    Color.Gray
            )
        }
    }
}