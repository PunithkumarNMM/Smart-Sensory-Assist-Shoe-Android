package com.smartshoe.app.socket

import io.socket.client.IO
import io.socket.client.Socket

object SocketManager {

    private const val SERVER_URL =
        "https://smartshoe-backend.onrender.com"

    private var socket: Socket? = null

    fun connect() {

        if (socket == null) {

            socket = IO.socket(SERVER_URL)

            socket?.connect()

        } else if (socket?.connected() == false) {

            socket?.connect()

        }

    }

    fun getSocket(): Socket? {

        connect()

        return socket

    }

    fun disconnect() {

        socket?.disconnect()

    }

}