package com.smartshoe.app.forgotpassword

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.ForgotPasswordRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Forgot Password",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                if (newPassword != confirmPassword) {

                    Toast.makeText(
                        navController.context,
                        "Passwords do not match",
                        Toast.LENGTH_LONG
                    ).show()

                    return@Button
                }

                loading = true

                scope.launch {

                    try {

                        val response =
                            RetrofitClient.api.forgotPassword(
                                ForgotPasswordRequest(
                                    email,
                                    newPassword
                                )
                            )

                        loading = false

                        if (response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            Toast.makeText(
                                navController.context,
                                "Password Updated Successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()

                        } else {

                            val error = response.errorBody()?.string()

                            Toast.makeText(
                                navController.context,
                                error ?: response.body()?.message ?: "Failed",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    } catch (e: Exception) {

                        loading = false

                        Toast.makeText(
                            navController.context,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            }
        ) {

            if (loading)
                CircularProgressIndicator()
            else
                Text("Reset Password")

        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back to Login")
        }

    }

}