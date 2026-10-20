package com.smartshoe.app.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.ChangePasswordRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                    if (showCurrent) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(
                            if (showCurrent)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                    if (showNew) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(
                            if (showNew)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation =
                    if (showConfirm) VisualTransformation.None
                    else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            if (showConfirm)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    if (newPassword != confirmPassword) {
                        Toast.makeText(
                            context,
                            "Passwords do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.changePassword(
                                    ChangePasswordRequest(
                                        currentPassword,
                                        newPassword
                                    )
                                )

                            if (response.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "Password changed successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack()

                            } else {

                                Toast.makeText(
                                    context,
                                    "Failed to change password",
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
                Text("Change Password")
            }

        }

    }
}