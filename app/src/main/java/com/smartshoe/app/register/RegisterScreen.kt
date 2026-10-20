package com.smartshoe.app.register

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
import com.smartshoe.app.model.RegisterRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController
) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                loading = true

                scope.launch {

                    try {

                        val response =
                            RetrofitClient.api.register(
                                RegisterRequest(
                                    name,
                                    email,
                                    phone,
                                    password
                                )
                            )

                        loading = false

                        if (response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            Toast.makeText(
                                navController.context,
                                "Registration Successful",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()

                        } else {

                            val error = response.errorBody()?.string()

                            Toast.makeText(
                                navController.context,
                                error ?: response.body()?.message ?: "Registration Failed",
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

            },
            modifier = Modifier.fillMaxWidth()
        ) {

            if (loading) {

                CircularProgressIndicator()

            } else {

                Text("Register")

            }

        }

    }

}