package com.smartshoe.app.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {

    private const val TAG = "SocketManager"
    private const val SERVER_URL = "https://smartshoe-backend.onrender.com"

    val socket: Socket by lazy {
        IO.socket(SERVER_URL)
    }

    fun connect() {
        if (!socket.connected()) {

            socket.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "✅ Connected to server")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "❌ Disconnected from server")
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) {
                Log.e(TAG, "⚠ Connection Error")
            }

            socket.connect()
        }
    }

    fun disconnect() {
        socket.disconnect()
    }
}