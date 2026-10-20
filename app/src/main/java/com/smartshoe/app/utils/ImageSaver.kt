package com.smartshoe.app.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import okhttp3.ResponseBody
import java.io.InputStream

object ImageSaver {

    fun saveImage(
        context: Context,
        responseBody: ResponseBody
    ): Boolean {

        return try {

            val inputStream: InputStream = responseBody.byteStream()

            val bitmap = BitmapFactory.decodeStream(inputStream)

            val values = ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "SmartShoe_${System.currentTimeMillis()}.jpg"
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/jpeg"
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "Pictures/SmartShoe"
                    )

                }

            }

            val uri =
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )

            uri?.let {

                context.contentResolver.openOutputStream(it)
                    ?.use { output ->

                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            output
                        )

                    }

            }

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false

        }

    }

}