package com.smartshoe.app.guardian

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smartshoe.app.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun EditGuardianScreen(
    navController: NavController,
    guardianId: String
) {

    val context = navController.context
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isPrimary by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        scope.launch {

            try {

                val response =
                    RetrofitClient.api.getGuardianById(guardianId)

                loading = false

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    val guardian = response.body()!!.data

                    name = guardian.name
                    relationship = guardian.relationship
                    phone = guardian.phone
                    email = guardian.email
                    isPrimary = guardian.isPrimary

                } else {

                    Toast.makeText(
                        context,
                        "Unable to load guardian",
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

    if (loading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Edit Guardian",
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
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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

                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.updateGuardian(

                                    guardianId,

                                    com.smartshoe.app.model.GuardianRequest(
                                        name = name,
                                        relationship = relationship,
                                        phone = phone,
                                        email = email,
                                        isPrimary = isPrimary
                                    )

                                )

                            if (
                                response.isSuccessful &&
                                response.body()?.success == true
                            ) {

                                Toast.makeText(
                                    context,
                                    "Guardian Updated Successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack()

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
                Text("UPDATE")
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    scope.launch {

                        try {

                            val response =
                                RetrofitClient.api.deleteGuardian(guardianId)

                            if (response.isSuccessful) {

                                Toast.makeText(
                                    context,
                                    "Guardian Deleted Successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack()

                            } else {

                                Toast.makeText(
                                    context,
                                    "Delete Failed",
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
                Text("DELETE")
            }

        }

    }

}