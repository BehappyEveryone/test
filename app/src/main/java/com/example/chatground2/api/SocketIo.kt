package com.example.chatground2.api

import io.socket.client.IO
import io.socket.client.Socket

object SocketIo {
    val opts = IO.Options()
    val mSocket: Socket = IO.socket(IpAddress.BaseURL, opts)
}