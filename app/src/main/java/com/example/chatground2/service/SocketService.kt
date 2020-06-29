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
import com.example.chatground2.model.KeyName.joinText
import com.example.chatground2.model.KeyName.socketMakeRoom
import com.example.chatground2.model.KeyName.socketMatchMaking
import com.example.chatground2.model.KeyName.socketMessage
import com.example.chatground2.model.KeyName.intentMessageValue
import com.example.chatground2.model.KeyName.socketOfferSubject
import com.example.chatground2.model.KeyName.intentOfferSubjectValue
import com.example.chatground2.model.KeyName.socketPresentationOrder
import com.example.chatground2.model.KeyName.intentPresentationOrderValue
import com.example.chatground2.model.KeyName.socketReVoting
import com.example.chatground2.model.KeyName.intentReVotingValue
import com.example.chatground2.model.KeyName.socketResult
import com.example.chatground2.model.KeyName.intentResultValue
import com.example.chatground2.model.KeyName.socketRoomInfoChange
import com.example.chatground2.model.KeyName.intentRoomInfoChangeValue
import com.example.chatground2.model.KeyName.messageText
import com.example.chatground2.model.KeyName.roomInfoText
import com.example.chatground2.model.KeyName.roomText
import com.example.chatground2.model.KeyName.socketOnConnect
import com.example.chatground2.model.KeyName.socketOnDisconnect
import com.example.chatground2.model.KeyName.typeImageText
import com.example.chatground2.model.KeyName.typeStrategicImageText
import com.example.chatground2.model.KeyName.typeStrategicVideoText
import com.example.chatground2.model.KeyName.typeVideoText
import com.example.chatground2.model.KeyName.userText
import com.example.chatground2.model.KeyName.usersText
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.ChatRoomInfoDto
import com.example.chatground2.model.dto.ChatSystemOrderDto


class SocketService : Service() {

    var mBinder: IBinder = SocketBinder()

    private var shared: Shared? = null

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
        SocketIo.mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect)
        SocketIo.mSocket.on(socketMakeRoom, onMakeRoom)
        SocketIo.mSocket.on(socketMatchMaking, onMatchMaking)
        SocketIo.mSocket.on(socketMessage, onMessage)
        SocketIo.mSocket.on(socketRoomInfoChange, onRoomInfoChange)
        SocketIo.mSocket.on(socketOfferSubject, onOfferSubject)
        SocketIo.mSocket.on(socketPresentationOrder, onPresentationOrder)
        SocketIo.mSocket.on(socketReVoting, onReVoting)
        SocketIo.mSocket.on(socketResult, onResult)
    }

    private fun initialize() {
        shared = Shared(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()

        SocketIo.mSocket.off(Socket.EVENT_CONNECT, onConnect)
        SocketIo.mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect)
        SocketIo.mSocket.off(socketMakeRoom, onMakeRoom)
        SocketIo.mSocket.off(socketMatchMaking, onMatchMaking)
        SocketIo.mSocket.off(socketMessage, onMessage)
        SocketIo.mSocket.off(socketRoomInfoChange, onRoomInfoChange)
        SocketIo.mSocket.off(socketOfferSubject, onOfferSubject)
        SocketIo.mSocket.off(socketPresentationOrder, onPresentationOrder)
        SocketIo.mSocket.off(socketReVoting, onReVoting)
        SocketIo.mSocket.off(socketResult, onResult)
    }

    fun isConnect(): Boolean = SocketIo.mSocket.connected()

    fun connectSocket() {
        try {
            if (!SocketIo.mSocket.connected()) {
                SocketIo.mSocket.connect()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun disconnectSocket() {
        SocketIo.mSocket.disconnect()
    }

    private val onMatchMaking = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val data: JSONObject =
            JSONObject().put(roomText, receivedData[roomText]).put(userText, shared?.getUser()?._id)
        SocketIo.mSocket.emit(joinText, data)
    }

    private val onMakeRoom = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        SocketIo.room = receivedData[roomText].toString()
        val chatIntent: Intent = Intent(this, ChatGroundActivity::class.java)
        chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        chatIntent.putExtra(usersText, receivedData.getJSONArray(usersText).toString())
        startActivity(chatIntent)
    }

    private val onRoomInfoChange = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        shared?.gsonFromJson(receivedData.get(roomInfoText).toString(), ChatRoomInfoDto::class.java)
            ?.let { it1 ->
                val intent: Intent = Intent(socketRoomInfoChange)
                intent.putExtra(intentRoomInfoChangeValue, it1)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
    }

    private val onMessage = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        shared?.gsonFromJson(receivedData.toString(), ChatDto::class.java)?.let { it1 ->
            val binaryData = Base64.decode(it1.binaryData.toString(), Base64.DEFAULT)

            if (it1.type == typeImageText || it1.type == typeStrategicImageText) {
                val path = makeDirAndSaveFile(binaryData, 0)
                it1.content = "file://$path"
            }
            if (it1.type == typeVideoText || it1.type == typeStrategicVideoText) {
                val path = makeDirAndSaveFile(binaryData, 1)
                if (path != null) {
                    it1.content = path
                }
            }
            it1.binaryData = null

            if (shared?.getMessage().isNullOrEmpty()) {
                shared?.setSharedPreference(messageText, JSONArray().put(shared?.gsonToJson(it1)).toString())
            } else {
                val jsonArray = JSONArray(shared?.getMessage())
                jsonArray.put(shared?.gsonToJson(it1))
                shared?.setSharedPreference(messageText, jsonArray.toString())
            }
            shared?.editorCommit()

            val intent: Intent = Intent(socketMessage)
            intent.putExtra(intentMessageValue, it1)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    private val onOfferSubject = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent(socketOfferSubject)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(intentOfferSubjectValue, receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onPresentationOrder = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        shared?.gsonFromJson(receivedData.toString(), ChatSystemOrderDto::class.java)?.let { it1 ->
            val intent: Intent = Intent(socketPresentationOrder)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(intentPresentationOrderValue, it1)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    private val onReVoting = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent(socketReVoting)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(intentReVotingValue, receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onResult = Emitter.Listener {
        val receivedData = it[0] as JSONObject

        val intent: Intent = Intent(socketResult)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(intentResultValue, receivedData.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onConnect = Emitter.Listener {
        //커넥트
        val intent: Intent = Intent(socketOnConnect)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private val onDisconnect = Emitter.Listener {
        val intent: Intent = Intent(socketOnDisconnect)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun makeDirAndSaveFile(binaryData: ByteArray, type: Int): String? {
        val direct = File(Environment.getExternalStorageDirectory().toString() + "/ChatGround")

        if (!direct.exists()) {
            val wallpaperDirectory =
                File(Environment.getExternalStorageDirectory().toString() + "/ChatGround")
            wallpaperDirectory.mkdirs()
        }

        val file = when (type) {
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