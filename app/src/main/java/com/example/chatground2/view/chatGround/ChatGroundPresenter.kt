package com.example.chatground2.view.chatGround

import android.content.*
import android.net.Uri
import android.os.IBinder
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.adapter.adapterContract.ChatAdapterContract
import com.example.chatground2.adapter.adapterContract.ChatUserAdapterContract
import com.example.chatground2.api.SocketIo
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.ChatUserDto
import com.example.chatground2.service.SocketService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class ChatGroundPresenter(
    private val context: Context,
    val view: ChatGroundContract.IChatGroundView
) : ChatGroundContract.IChatGroundPresenter {

    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery: Gallery = Gallery(context)
    private var gson:Gson = Gson()

    private var socketService: SocketService? = null
    private val intentFilter: IntentFilter = IntentFilter()
    private var strategic: Boolean = false

    override var adapterChatModel: ChatAdapterContract.Model? = null
    override var adapterChatView: ChatAdapterContract.View? = null
        set(value) {//커스텀 접근자
            field = value
            field?.onVideoClickFunc = { clickVideo(it) }
        }
    override var adapterChatUserModel: ChatUserAdapterContract.Model? = null
    override var adapterChatUserView: ChatUserAdapterContract.View? = null

    init {
        //받을 브로드캐스트 리시버
        intentFilter.addAction("onMessage")
        intentFilter.addAction("onOfferSubject")
        intentFilter.addAction("onRoomInfoChange")
        intentFilter.addAction("onPresentationOrder")
        intentFilter.addAction("reVoting")
        intentFilter.addAction("result")
    }

    override fun getIntent(intent: Intent) {
        val users = JSONArray(intent.getStringExtra("users"))
        val arrayList = ArrayList<ChatUserDto>()
        for (i in 0 until users.length()) {
            arrayList.add(shared?.gsonFromJson(users[i].toString(), ChatUserDto::class.java))
        }
        adapterChatUserModel?.addItems(arrayList)
        adapterChatUserView?.notifyAdapter()
    }

    override fun bindService() {
        val intent = Intent(context, SocketService::class.java)
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun unbindService() {
        context.unbindService(mConnection)
    }

    override fun setBroadCastReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter)
    }

    override fun removeBroadCastReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    override fun drawerClick() {
        view.openDrawer()
    }

    override fun getMessages() {
        val arrayList = ArrayList<ChatDto>()
        if (shared.getMessage() != null) {
            val jsonArray = JSONArray(shared.getMessage())
            for (i in 0 until jsonArray.length()) {
                arrayList.add(gson.fromJson(jsonArray[i].toString(), ChatDto::class.java))
            }
            adapterChatModel?.addItems(arrayList)
            adapterChatView?.notifyAdapter()
            adapterChatModel?.getItemSize()?.let { view.setChatScrollPosition(it - 1) }
        }
    }

    override fun removeMessages() {
        adapterChatModel?.clearItems()
    }

    override fun setOpinion(boolean: Boolean) {
        if (boolean) {//agree 버튼을 눌렀을 때
            if (view.getAgreeButtonSelected()) {//agree 버튼이 이미 눌러져 있으면
                view.setAgreeButtonSelected(false)
            } else {//agree 버튼이 이미 눌러져 있는 상태가 아니라면
                view.setAgreeButtonSelected(true)
            }
            view.setOpposeButtonSelected(false)
        } else {
            if (view.getOpposeButtonSelected()) {
                view.setOpposeButtonSelected(false)
            } else {
                view.setOpposeButtonSelected(true)
            }
            view.setAgreeButtonSelected(false)
        }
    }

    override fun plusClick() {
        view.plusDialog()
    }

    override fun imageGalleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        val file: File = File(path)
        sendImage(file)
    }

    override fun videoGalleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        val file: File = File(path)
        sendVideo(file)
    }

    override fun leave() {
        try {
            val data: JSONObject = JSONObject()
                .put("user", shared.getUser()._id)
                .put("room", SocketIo.room)
            socketService?.socketEmit("leaveRoom", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        reset()
    }

    override fun leaveDialog() {
        view.leaveDialog()
    }

    override fun checkCameraPermission(num: Int) {
        when (permission.checkCameraPermission()) {
            0 -> {//권한을 이미 허용
                when (num) {
                    0 -> view.openGallery()
                    1 -> view.openVideo()
                }
            }
            1 -> {//이전에 이미 권한이 거부됨
                view.toastMessage("권한을 허가해주십시오.")
                permission.setupPermissions()
            }
            3 -> {//최초로 권한 요청
                permission.makeRequest()
            }
        }
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    private fun reset() {
        SocketIo.opinion = "neutrality"
        SocketIo.room = null
        shared.editorRemove("message")
        shared.editorCommit()
        view.finishActivity()
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "onMessage" -> {
                    adapterChatModel?.addItem(
                        shared.gsonFromJson(
                            intent.getStringExtra("onMessageValue"),
                            ChatDto::class.java
                        )
                    )
                    adapterChatView?.notifyAdapter()
                    adapterChatModel?.getItemSize()?.let { view.setChatScrollPosition(it - 1) }
                }
                "onRoomInfoChange" -> {
                    val json = JSONObject(intent.getStringExtra("onRoomInfoChangeValue"))
                    println("json : $json")
                    val users = json.getJSONObject("roomInfo").getJSONArray("users")

                    val arrayList = ArrayList<ChatUserDto>()
                    for (i in 0 until users.length()) {
                        val user = shared.gsonFromJson(users[i].toString(), ChatUserDto::class.java)
                        if (user._id == shared.getUser()._id) {
                            SocketIo.opinion = user.opinion.toString()
                        }
                        arrayList.add(user)
                    }
                    adapterChatUserModel?.clearItems()
                    adapterChatUserModel?.addItems(arrayList)
                    adapterChatUserView?.notifyAdapter()
                }
                "onOfferSubject" -> {
                    val json = JSONObject(intent.getStringExtra("onOfferSubjectValue"))
                    val subject = json.get("subject").toString()
                    val time = json.get("time").toString()

                    CoroutineScope(Dispatchers.Main).launch {
                        view.setEnable(false)

                        for (i: Int in 0..subject.length) {
                            view.setSubjectText(subject.substring(0, i))
                            delay(100)
                        }

                        view.setOpinionVisible(true)
                        for (i: Int in time.toInt() / 1000 downTo 0) {
                            if (i == 0) {
                                view.setOpinionVisible(false)

                                val opinion: String = when {
                                    view.getAgreeButtonSelected() -> "agree"
                                    view.getOpposeButtonSelected() -> "oppose"
                                    else -> "neutrality"
                                }

                                val data: JSONObject =
                                    JSONObject().put("user", shared.getUser()._id)
                                        .put("room", SocketIo.room)
                                        .put("opinion", opinion)
                                socketService?.socketEmit("opinionResult", data)
                                SocketIo.opinion = opinion
                                view.setAgreeButtonSelected(false)
                                view.setOpposeButtonSelected(false)
                            }
                            if (i < 10) {
                                view.setTimeText("00:0$i")
                            } else {
                                view.setTimeText("00:$i")
                            }
                            delay(1000)//1000
                        }
                    }
                }
                "onPresentationOrder" -> {
                    val json = JSONObject(intent.getStringExtra("onPresentationOrderValue"))
                    val time = json.get("time").toString()
                    val order = json.get("order").toString()
                    val speaking = json.get("speaking")

                    CoroutineScope(Dispatchers.Main).launch {
                        if (order == "strategicTimeComplete" || order == "strategicTimeComplete2") {
                            strategic = true
                        }
                        when (speaking) {
                            "all" -> {
                                view.setEnable(true)
                            }
                            "agree" -> {
                                if (SocketIo.opinion == "agree") {
                                    view.setEnable(true)
                                } else {
                                    view.setEnable(false)
                                }
                            }
                            "oppose" -> {
                                if (SocketIo.opinion == "oppose") {
                                    view.setEnable(true)
                                } else {
                                    view.setEnable(false)
                                }
                            }
                        }

                        for (i: Int in time.toInt() / 1000 downTo 0) {
                            val sec: Int = i % 60
                            val min: Int = i / 60
                            when {
                                min == 0 && sec == 0 -> {
                                    view.setEnable(false)
                                    view.setTimeText("00:00")
                                    if (strategic) {
                                        strategic = false
                                    }

                                    val data: JSONObject = JSONObject().put("room", SocketIo.room)
                                        .put("user", shared.getUser()._id)
                                    socketService?.socketEmit(order, data)
                                }
                                min == 0 -> when {
                                    sec < 10 -> view.setTimeText("00:0$sec")
                                    else -> view.setTimeText("00:$sec")
                                }
                                min < 10 -> when {
                                    sec < 10 -> view.setTimeText("0$min:0$sec")
                                    else -> view.setTimeText("0$min:$sec")
                                }
                                else -> when {
                                    sec < 10 -> view.setTimeText("$min:0$sec")
                                    else -> view.setTimeText("$min:$sec")
                                }
                            }
                            delay(1000)
                        }
                    }
                }
                "reVoting" -> {
                    val json = JSONObject(intent.getStringExtra("reVotingValue"))
                    val time = json.get("time").toString()

                    CoroutineScope(Dispatchers.Main).launch {
                        view.setEnable(false)

                        view.setOpinionVisible(true)
                        for (i: Int in time.toInt() / 1000 downTo 0) {
                            if (i == 0) {
                                view.setOpinionVisible(false)

                                val reVote: String = when {
                                    view.getAgreeButtonSelected() -> "agree"
                                    view.getOpposeButtonSelected() -> "oppose"
                                    else -> "neutrality"
                                }

                                val data: JSONObject =
                                    JSONObject().put("user", shared.getUser()._id)
                                        .put("room", SocketIo.room)
                                        .put("reVote", reVote)
                                socketService?.socketEmit("reVoteResult", data)
                                view.setAgreeButtonSelected(false)
                                view.setOpposeButtonSelected(false)
                            }
                            if (i < 10) {
                                view.setTimeText("00:0$i")
                            } else {
                                view.setTimeText("00:$i")
                            }
                            delay(1000)//1000
                        }
                    }
                }

                "result" -> {
                    val json = JSONObject(intent.getStringExtra("resultValue"))

                    when (json.get("winner").toString()) {
                        "neutrality" -> {
                            view.setResultText("DRAW")
                        }
                        "agree" -> {
                            if (SocketIo.opinion == "agree") {
                                view.setResultText("WIN")
                            } else {
                                view.setResultText("LOSE")
                            }
                        }
                        "oppose" -> {
                            if (SocketIo.opinion == "oppose") {
                                view.setResultText("WIN")
                            } else {
                                view.setResultText("LOSE")
                            }
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(5000)

                        reset()
                    }
                }
            }
        }
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            println("ChatGround 서비스 끊김")
            socketService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("ChatGround 서비스 연결")

            val mBinder = service as SocketService.SocketBinder
            socketService = mBinder.getService()

            val data: JSONObject =
                JSONObject().put("room", SocketIo.room).put("user", shared.getUser()._id)
            socketService?.socketEmit("onMakeRoom", data)
        }
    }

    private fun convertFileToByte(file: File): ByteArray {
        var fis: FileInputStream? = null

        try {
            fis = FileInputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return fis.use { input ->
            input!!.readBytes()
        }
//        return FileInputStream(file).use { input -> input.readBytes() }
    }

    override fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            if (strategic) {
                val data: JSONObject =
                    JSONObject().put("type", "strategic").put("content", message)
                        .put("user", shared.getUser()._id)
                        .put("room", SocketIo.room)
                socketService?.socketEmit("sendMessage", data)
            } else {
                val data: JSONObject =
                    JSONObject().put("type", "text").put("content", message)
                        .put("user", shared.getUser()._id)
                        .put("room", SocketIo.room)
                socketService?.socketEmit("sendMessage", data)
            }

            view.setMessageClear()
        } else {
            view.toastMessage("내용을 입력해주세요!")
        }
    }

    private fun sendImage(file: File) {

        if (strategic) {
            try {
                val binaryData: String =
                    Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)

                val data: JSONObject = JSONObject().put("type", "strategic").put("content", "image")
                    .put("user", shared.getUser()._id)
                    .put("room", SocketIo.room)
                    .put("binaryData", binaryData)
                socketService?.socketEmit("sendMessage", data)
            } catch (e: JSONException) {
                println(e)
            }
        } else {
            try {
                val binaryData: String =
                    Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)

                val data: JSONObject = JSONObject().put("type", "image").put("content", "image")
                    .put("user", shared.getUser()._id)
                    .put("room", SocketIo.room)
                    .put("binaryData", binaryData)
                socketService?.socketEmit("sendMessage", data)
            } catch (e: JSONException) {
                println(e)
            }
        }
    }

    private fun sendVideo(file: File) {
        if (strategic) {
            try {
                val binaryData: String =
                    Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)

                val data: JSONObject = JSONObject().put("type", "strategic").put("content", "video")
                    .put("user", shared.getUser()._id)
                    .put("room", SocketIo.room)
                    .put("binaryData", binaryData)
                socketService?.socketEmit("sendMessage", data)
            } catch (e: JSONException) {
                println(e)
            }
        } else {
            try {
                val binaryData: String =
                    Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)

                val data: JSONObject = JSONObject().put("type", "video").put("content", "video")
                    .put("user", shared.getUser()._id)
                    .put("room", SocketIo.room)
                    .put("binaryData", binaryData)
                socketService?.socketEmit("sendMessage", data)
            } catch (e: JSONException) {
                println(e)
            }
        }
    }

    private fun clickVideo(position: Int) {
        try {
            val videoFile = File(adapterChatModel?.getItem(position)?.content)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "com.example.chatground2.fileprovider",
                videoFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "video/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            view.toastMessage("파일을 찾을 수 없습니")
        }
    }
}