package com.smartshoe.app.guardian

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.model.GuardianRequest
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun AddGuardianScreen(
    navController: NavController
) {

    val scope = rememberCoroutineScope()
    val context = navController.context

    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isPrimary by remember { mutableStateOf(false) }

    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Add Guardian",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Guardian Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = relationship,
            onValueChange = { relationship = it },
            label = { Text("Relationship") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Primary Guardian")

            Switch(
                checked = isPrimary,
                onCheckedChange = {
                    isPrimary = it
                }
            )

        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                loading = true

                scope.launch {

                    try {

                        val response =
                            RetrofitClient.api.addGuardian(

                                GuardianRequest(
                                    name = name,
                                    relationship = relationship,
                                    phone = phone,
                                    email = email,
                                    isPrimary = isPrimary
                                )

                            )

                        loading = false

                        if (
                            response.isSuccessful &&
                            response.body()?.success == true
                        ) {

                            Toast.makeText(
                                context,
                                "Guardian Added",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.popBackStack()

                        } else {

                            Toast.makeText(
                                context,
                                "Failed",
                                Toast.LENGTH_LONG
                            ).show()

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

            }
        ) {

            if (loading)
                CircularProgressIndicator()
            else
                Text("SAVE")

        }

    }

}