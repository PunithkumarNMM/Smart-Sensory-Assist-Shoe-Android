package com.smartshoe.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {

    AlertDialog(

        onDismissRequest = onDismiss,

        icon = {

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50)
            )

        },

        title = {

            Text(
                title,
                fontWeight = FontWeight.Bold
            )

        },

        text = {

            Text(message)

        },

        confirmButton = {

            Button(
                onClick = onDismiss
            ) {

                Text("OK")

            }

        }

    )

}