package com.example.chatground2.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatground2.api.SocketIo
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import com.example.chatground2.view.chatGround.ChatGroundActivity
import org.json.JSONArray
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import android.os.Environment
import android.util.Base64
import com.example.chatground2.`class`.Shared


class SocketService : Service() {

    var mBinder: IBinder = SocketBinder()

    private var shared:Shared? = null

    class SocketBinder : Binder() {
        fun getService(): SocketService { // 서비스 객체를 리턴
            return SocketService()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        initialize()

        SocketIo.mSocket.on(Socket.EVENT_CONNECT, onConnect)
        SocketIo.mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect)
        SocketIo.mSocket.on("makeRoom", onMakeRoom)
        SocketIo.mSocket.on("matchMaking", onMatchMaking)
        SocketIo.mSocket.on("message", onMessage)
        SocketIo.mSocket.on("roomInfoChange", onRoomInfoChange)
        SocketIo.mSocket.on("offerSubject", onOfferSubject)
        SocketIo.mSocket.on("presentationOrder", onPresentationOrder)
        SocketIo.mSocket.on("reVoting", reVoting)
        SocketIo.mSocket.on("result", result)
    }

    private fun initialize() {
        shared = Shared(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()

        SocketIo.mSocket.off(Socket.EVENT_CONNECT, onConnect)
        SocketIo.mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect)
        SocketIo.mSocket.off("makeRoom", onMakeRoom)
        SocketIo.mSocket.off("matchMaking", onMatchMaking)
        SocketIo.mSocket.off("message", onMessage)
        SocketIo.mSocket.off("roomInfoChange", onRoomInfoChange)
        SocketIo.mSocket.off("offerSubject", onOfferSubject)
        SocketIo.mSocket.off("presentationOrder", onPresentationOrder)
        SocketIo.mSocket.off("reVoting", reVoting)
        SocketIo.mSocket.off("result", result)
    }

    fun isConnect(): Boolean = SocketIo.mSocket.connected()

    fun connectSocket() {
        println("소켓연결시도")
        try {
            if (!SocketIo.mSocket.connected()) {
                SocketIo.mSocket.connect()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun disconnectSocket() {
        println("소켓연결해제시도")
        SocketIo.mSocket.disconnect()
    }

    private val onMatchMaking = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val data: JSONObject =
            JSONObject().put("room", receivedData["room"]).put("user", shared?.getUser()?._id)
        SocketIo.mSocket.emit("join", data)
    }

    private val onMakeRoom = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        SocketIo.room = receivedData["room"].toString()
        val chatIntent: Intent = Intent(this, ChatGroundActivity::class.java)
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        chatIntent.putExtra("users", receivedData.getJSONArray("users").toString())
        startActivity(chatIntent)
    }

    private val onRoomInfoChange = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent("onRoomInfoChange")
        intent.putExtra("onRoomInfoChangeValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onMessage = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        println("메세지 : " + receivedData["type"])

        if(receivedData["type"] == "image"){
            val binaryData = Base64.decode(receivedData["binaryData"].toString(),Base64.DEFAULT)
            val path = makeDirAndSaveFile(binaryData,0)
            receivedData.put("content", "file://$path")
        }
        if(receivedData["type"] == "video"){
            val binaryData = Base64.decode(receivedData["binaryData"].toString(),Base64.DEFAULT)
            val path = makeDirAndSaveFile(binaryData,1)
            receivedData.put("content", "$path")
        }

        if (shared?.getMessage() != null) {
            val jsonArray = JSONArray(shared?.getMessage())
            jsonArray.put(receivedData)
            shared?.setSharedPreference("message",jsonArray.toString())
        } else {
            shared?.setSharedPreference("message",JSONArray().put(receivedData).toString())
        }
        shared?.editorCommit()

        val intent: Intent = Intent("onMessage")
        intent.putExtra("onMessageValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onOfferSubject = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent("onOfferSubject")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("onOfferSubjectValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onPresentationOrder = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent("onPresentationOrder")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("onPresentationOrderValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val reVoting = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent("reVoting")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("reVotingValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val result = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent("result")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("resultValue", receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onConnect = Emitter.Listener {
        //커넥트
        val intent: Intent = Intent("onConnect")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onDisconnect = Emitter.Listener {
        val intent: Intent = Intent("onDisconnect")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun makeDirAndSaveFile(binaryData: ByteArray,type:Int): String? {
        val direct = File(Environment.getExternalStorageDirectory().toString() + "/ChatGround")

        if (!direct.exists()) {
            val wallpaperDirectory =
                File(Environment.getExternalStorageDirectory().toString() + "/ChatGround")
            wallpaperDirectory.mkdirs()
        }

        val file = when(type){
            0 -> File("${Environment.getExternalStorageDirectory()}/ChatGround/${System.currentTimeMillis()}.png")
            1 -> File("${Environment.getExternalStorageDirectory()}/ChatGround/${System.currentTimeMillis()}.mp4")
            else -> null
        }

        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }

        fos.use { output ->
            output?.write(binaryData)
            output?.flush()
            output?.close()
        }

        return file?.path
    }

    fun socketEmit(name: String, value: JSONObject) {
        SocketIo.mSocket.emit(name, value)
    }
}