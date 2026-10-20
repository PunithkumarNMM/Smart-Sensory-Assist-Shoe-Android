package com.smartshoe.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Esp32Retrofit {

    private const val BASE_URL = "http://10.122.91.219/"

    val api: Esp32Api by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Esp32Api::class.java)

    }

}