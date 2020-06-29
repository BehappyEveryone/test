package com.example.chatground2.api

import com.example.chatground2.model.Constant.neutrality
import io.socket.client.IO
import io.socket.client.Socket

object SocketIo {
    val opts = IO.Options()
    val mSocket: Socket = IO.socket(IpAddress.BaseURL, opts)
    var room:String? = null
    var opinion:String = neutrality
}