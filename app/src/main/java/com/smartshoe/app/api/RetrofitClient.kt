package com.smartshoe.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.smartshoe.app.MyApplication
import com.smartshoe.app.utils.SessionManager

object RetrofitClient {

    // ==========================
    // CHANGE ONLY IF BACKEND URL CHANGES
    // ==========================
    private const val BASE_URL = "https://smartshoe-backend.onrender.com/"

    // ==========================
    // HTTP LOGGER
    // ==========================
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // ==========================
    // OKHTTP CLIENT
    // ==========================
    private val client = OkHttpClient.Builder()

        .connectTimeout(60, TimeUnit.SECONDS)

        .readTimeout(60, TimeUnit.SECONDS)

        .writeTimeout(60, TimeUnit.SECONDS)

        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->

            val context = MyApplication.instance

            val token = SessionManager(context).getToken()

            val requestBuilder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader(
                    "Authorization",
                    "Bearer $token"
                )
            }

            chain.proceed(requestBuilder.build())
        }

        .addInterceptor(logger)

        .build()

    // ==========================
    // RETROFIT
    // ==========================
    private val retrofit = Retrofit.Builder()

        .baseUrl(BASE_URL)

        .client(client)

        .addConverterFactory(
            GsonConverterFactory.create()
        )

        .build()

    // ==========================
    // API
    // ==========================
    val api: ApiService =
        retrofit.create(ApiService::class.java)

}