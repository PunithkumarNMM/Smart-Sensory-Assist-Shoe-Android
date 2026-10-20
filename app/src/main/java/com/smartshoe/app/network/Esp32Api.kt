package com.smartshoe.app.network

import retrofit2.Response
import retrofit2.http.GET

interface Esp32Api {

    @GET("/")
    suspend fun getData(): Response<Esp32Data>

}