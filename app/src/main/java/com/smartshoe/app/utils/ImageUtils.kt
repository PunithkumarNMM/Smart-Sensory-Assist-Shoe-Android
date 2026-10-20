package com.smartshoe.app.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun uriToMultipart(
    context: Context,
    uri: Uri
): MultipartBody.Part {

    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File.createTempFile("profile", ".jpg", context.cacheDir)

    file.outputStream().use { output ->
        inputStream?.copyTo(output)
    }

    val requestBody =
        file.asRequestBody("image/*".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "image",
        file.name,
        requestBody
    )
}