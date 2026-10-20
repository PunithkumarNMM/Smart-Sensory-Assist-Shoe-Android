package com.smartshoe.app.gps

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.socket.SocketManager
import com.smartshoe.app.utils.SessionManager

import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val session = remember {
        SessionManager(context)
    }

    val scope = rememberCoroutineScope()

    var latitude by remember {
        mutableStateOf(12.9716)
    }

    var longitude by remember {
        mutableStateOf(77.5946)
    }

    var speed by remember {
        mutableStateOf(0.0)
    }

    var accuracy by remember {
        mutableStateOf(0.0)
    }

    var battery by remember {
        mutableStateOf(82)
    }

    var online by remember {
        mutableStateOf(true)
    }

    var lastUpdated by remember {
        mutableStateOf("Just now")
    }

    val cameraState =
        rememberCameraPositionState()

    val markerState =
        rememberMarkerState(
            position = LatLng(latitude, longitude)
        )

    val routePoints =
        remember {
            mutableStateListOf<LatLng>()
        }

    val pulse =
        rememberInfiniteTransition(
            label = "online"
        )

    val onlineAlpha by pulse.animateFloat(

        initialValue = 0.35f,

        targetValue = 1f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(900),

                repeatMode =
                    RepeatMode.Reverse

            ),

        label = "pulse"

    )

    LaunchedEffect(Unit) {

        SocketManager.connect()

        while (true) {

            try {

                val deviceId =
                    session.getDeviceId()

                if (!deviceId.isNullOrEmpty()) {

                    val response =
                        RetrofitClient.api
                            .getLiveLocation(deviceId)

                    if (

                        response.isSuccessful &&

                        response.body()?.success == true

                    ) {

                        val gps =
                            response.body()!!.location

                        latitude = gps.latitude

                        longitude = gps.longitude

                        speed = gps.speed

                        accuracy = gps.accuracy

                        lastUpdated = "Just now"

                    }

                }

            } catch (e: Exception) {

                e.printStackTrace()

            }

            delay(5000)

        }

    }

    DisposableEffect(Unit) {

        val listener =
            Emitter.Listener { args ->

                try {

                    val obj =
                        args[0] as org.json.JSONObject

                    latitude =
                        obj.getDouble("latitude")

                    longitude =
                        obj.getDouble("longitude")

                    speed =
                        obj.optDouble(
                            "speed",
                            0.0
                        )

                    accuracy =
                        obj.optDouble(
                            "accuracy",
                            0.0
                        )

                    online = true

                    lastUpdated = "Live"

                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }

        SocketManager
            .getSocket()
            ?.on(
                "gps:update",
                listener
            )

        onDispose {

            SocketManager
                .getSocket()
                ?.off(
                    "gps:update",
                    listener
                )

        }

    }

    LaunchedEffect(
        latitude,
        longitude
    ) {

        val point =
            LatLng(
                latitude,
                longitude
            )

        markerState.position = point

        routePoints.add(point)

        cameraState.animate(

            CameraUpdateFactory
                .newLatLngZoom(
                    point,
                    18f
                )

        )

    }
    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        text = "Live GPS Tracking",

                        fontWeight = FontWeight.Bold,

                        fontSize = 22.sp

                    )

                },

                navigationIcon = {

                    IconButton(

                        onClick = {

                            navController.popBackStack()

                        }

                    ) {

                        Icon(

                            Icons.Default.ArrowBack,

                            contentDescription = null

                        )

                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = Color(0xFF1565C0),

                    titleContentColor = Color.White,

                    navigationIconContentColor = Color.White

                )

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding)

                .background(

                    Brush.verticalGradient(

                        listOf(

                            Color(0xFFF6F9FF),

                            Color.White

                        )

                    )

                )

                .padding(16.dp)

        ) {

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(8.dp)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp)

                ) {

                    Text(

                        text = "Device Status",

                        style = MaterialTheme.typography.titleLarge,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Row(

                            verticalAlignment = Alignment.CenterVertically

                        ) {

                            Box(

                                modifier = Modifier

                                    .size(14.dp)

                                    .alpha(onlineAlpha)

                                    .background(

                                        if (online)

                                            Color(0xFF2ECC71)

                                        else

                                            Color.Red,

                                        CircleShape

                                    )

                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(

                                text =

                                    if (online)

                                        "ONLINE"

                                    else

                                        "OFFLINE",

                                color =

                                    if (online)

                                        Color(0xFF2ECC71)

                                    else

                                        Color.Red,

                                fontWeight = FontWeight.Bold

                            )

                        }

                        Text(

                            text = lastUpdated,

                            color = Color.Gray

                        )

                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    LinearProgressIndicator(

                        progress = { battery / 100f },

                        modifier = Modifier.fillMaxWidth()

                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(

                        text = "Battery : $battery%",

                        fontWeight = FontWeight.SemiBold

                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            GPSInfoCard(
                                "Latitude",
                                "%.6f".format(latitude),
                                Icons.Default.LocationOn
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            GPSInfoCard(
                                "Longitude",
                                "%.6f".format(longitude),
                                Icons.Default.MyLocation
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            GPSInfoCard(
                                "Speed",
                                "%.1f km/h".format(speed),
                                Icons.Default.Speed
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            GPSInfoCard(
                                "Accuracy",
                                "%.1f m".format(accuracy),
                                Icons.Default.GpsFixed
                            )
                        }

                    }

                }

            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),

                shape = RoundedCornerShape(28.dp),

                elevation = CardDefaults.cardElevation(10.dp)

            ) {

                GoogleMap(

                    modifier = Modifier.fillMaxSize(),

                    cameraPositionState = cameraState,

                    uiSettings = MapUiSettings(

                        zoomControlsEnabled = true,

                        compassEnabled = true,

                        myLocationButtonEnabled = true,

                        mapToolbarEnabled = true,

                        zoomGesturesEnabled = true,

                        scrollGesturesEnabled = true

                    ),

                    properties = MapProperties(

                        isMyLocationEnabled = true

                    )

                ) {

                    if (routePoints.size > 1) {

                        Polyline(

                            points = routePoints,

                            color = Color(0xFF1565C0),

                            width = 10f,

                            jointType = JointType.ROUND

                        )

                    }

                    Marker(

                        state = markerState,

                        title = "Smart Shoe",

                        snippet = "Live Location"

                    )

                }

            }
            Spacer(modifier = Modifier.height(16.dp))

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                elevation = CardDefaults.cardElevation(6.dp)

            ) {

                Column(

                    modifier = Modifier.padding(18.dp)

                ) {

                    Text(

                        text = "Tracking Information",

                        style = MaterialTheme.typography.titleMedium,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Text("Tracking")

                        Text(

                            if (online) "LIVE"

                            else "OFFLINE",

                            color =

                                if (online)

                                    Color(0xFF2ECC71)

                                else

                                    Color.Red,

                            fontWeight = FontWeight.Bold

                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Text("Speed")

                        Text("%.1f km/h".format(speed))

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(

                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement = Arrangement.SpaceBetween

                    ) {

                        Text("Accuracy")

                        Text("%.1f m".format(accuracy))

                    }

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(18.dp),

                onClick = {

                    scope.launch {

                        cameraState.animate(

                            CameraUpdateFactory.newLatLngZoom(

                                LatLng(

                                    latitude,

                                    longitude

                                ),

                                18f

                            )

                        )

                    }

                }

            ) {

                Icon(

                    Icons.Default.MyLocation,

                    contentDescription = null

                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(

                    "Center On Device",

                    fontWeight = FontWeight.Bold

                )

            }

        }

    }

}

@Composable
fun GPSInfoCard(

    title: String,

    value: String,

    icon: ImageVector

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor =

                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)

        )

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Box(

                modifier = Modifier

                    .size(46.dp)

                    .background(

                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),

                        CircleShape

                    ),

                contentAlignment = Alignment.Center

            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = MaterialTheme.colorScheme.primary,

                    modifier = Modifier.size(24.dp)

                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(

                text = value,

                style = MaterialTheme.typography.titleMedium,

                fontWeight = FontWeight.Bold

            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(

                text = title,

                style = MaterialTheme.typography.bodySmall,

                color = Color.Gray

            )

        }

    }

}