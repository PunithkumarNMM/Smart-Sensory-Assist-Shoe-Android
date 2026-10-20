package com.smartshoe.app

import android.app.Application
import com.smartshoe.app.socket.SocketManager

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        SocketManager.connect()
    }
}