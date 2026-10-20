package com.smartshoe.app.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.smartshoe.app.network.RetrofitClient
import com.smartshoe.app.utils.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@Composable
fun ProfileScreen(
    navController: NavController
) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri == null) return@rememberLauncherForActivityResult

            scope.launch {

                try {

                    val input =
                        context.contentResolver.openInputStream(uri)

                    val file =
                        File(context.cacheDir, "profile.jpg")

                    val output =
                        FileOutputStream(file)

                    input?.copyTo(output)

                    input?.close()
                    output.close()

                    val mimeType =
                        context.contentResolver.getType(uri)
                            ?: "image/jpeg"

                    val request =
                        file.asRequestBody(
                            mimeType.toMediaTypeOrNull()
                        )

                    val part =
                        MultipartBody.Part.createFormData(
                            "image",
                            file.name,
                            request
                        )

                    val response =
                        RetrofitClient.api.uploadProfileImage(part)

                    if (
                        response.isSuccessful &&
                        response.body()?.success == true
                    ) {

                        profileImage =
                            response.body()!!.imageUrl +
                                    "?t=" +
                                    System.currentTimeMillis()

                        Toast.makeText(
                            context,
                            "Profile image updated",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        Toast.makeText(
                            context,
                            "Upload Failed",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        context,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()

                }

            }

        }

    LaunchedEffect(Unit) {

        try {

            val response =
                RetrofitClient.api.getProfile()

            loading = false

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                val user =
                    response.body()!!.data

                name = user.name
                email = user.email
                profileImage = user.profileImage

            }

        } catch (e: Exception) {

            loading = false

            Toast.makeText(
                context,
                e.message,
                Toast.LENGTH_LONG
            ).show()

        }

    }
    if (loading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()

        }

    } else {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1565C0),
                            Color(0xFF42A5F5),
                            Color.White
                        )
                    )
                )
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Box(

                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable {

                        imagePicker.launch("image/*")

                    },

                contentAlignment = Alignment.Center

            ) {

                if (profileImage.isBlank()) {

                    Icon(

                        imageVector = Icons.Default.AccountCircle,

                        contentDescription = null,

                        modifier = Modifier.size(110.dp),

                        tint = MaterialTheme.colorScheme.primary

                    )

                } else {

                    AsyncImage(

                        model = profileImage,

                        contentDescription = "Profile Image",

                        modifier = Modifier.fillMaxSize(),

                        contentScale = ContentScale.Crop

                    )

                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(

                text = "Tap profile photo to change",

                color = Color.White.copy(alpha = 0.9f),

                style = MaterialTheme.typography.bodyMedium

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(

                text = name,

                fontSize = 28.sp,

                fontWeight = FontWeight.Bold,

                color = Color.White

            )

            Text(

                text = email,

                color = Color.White.copy(alpha = 0.9f)

            )

            Spacer(modifier = Modifier.height(30.dp))

            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(8.dp)

            ) {

                Column(

                    modifier = Modifier.padding(20.dp)

                ) {

                    Text(

                        text = "Personal Information",

                        style = MaterialTheme.typography.titleLarge,

                        fontWeight = FontWeight.Bold

                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(

                        value = name,

                        onValueChange = {

                            name = it

                        },

                        modifier = Modifier.fillMaxWidth(),

                        leadingIcon = {

                            Icon(
                                Icons.Default.Person,
                                contentDescription = null
                            )

                        },

                        label = {

                            Text("Full Name")

                        }

                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(

                        value = email,

                        onValueChange = {

                            email = it

                        },

                        modifier = Modifier.fillMaxWidth(),

                        label = {

                            Text("Email")

                        }

                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp),

                        onClick = {

                            scope.launch {

                                try {

                                    val response =
                                        RetrofitClient.api.updateProfile(

                                            UpdateProfileRequest(
                                                name,
                                                email
                                            )

                                        )

                                    if (
                                        response.isSuccessful &&
                                        response.body()?.success == true
                                    ) {

                                        Toast.makeText(
                                            context,
                                            "Profile Updated Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } else {

                                        Toast.makeText(
                                            context,
                                            "Update Failed",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }

                                } catch (e: Exception) {

                                    Toast.makeText(
                                        context,
                                        e.message,
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        }

                    ) {

                        Text("SAVE PROFILE")

                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp),

                        onClick = {

                            navController.navigate("changePassword")

                        }

                    ) {

                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("CHANGE PASSWORD")

                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        ),

                        onClick = {

                            SessionManager(context).clearSession()

                            navController.navigate("login") {

                                popUpTo(0) {
                                    inclusive = true
                                }

                                launchSingleTop = true

                            }

                        }

                    ) {

                        Icon(
                            Icons.Default.Logout,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("LOGOUT")

                    }

                    Spacer(modifier = Modifier.height(30.dp))

                }

            }

        }

    }
}