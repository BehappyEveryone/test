package com.example.chatground2.view.chatGround

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.IBinder
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatground2.adapter.adapterContract.ChatAdapterContract
import com.example.chatground2.adapter.adapterContract.ChatUserAdapterContract
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.ChatUserDto
import com.example.chatground2.model.dto.UserDto
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

    private var socketService: SocketService? = null
    private val intentFilter: IntentFilter = IntentFilter()
    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val spEdit: SharedPreferences.Editor = sp.edit()
    private val gson = Gson()
    private var strategic: Boolean = false
    private var c: Cursor? = null

    override var adapterChatModel: ChatAdapterContract.Model? = null
    override var adapterChatView: ChatAdapterContract.View? = null
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
            arrayList.add(gson.fromJson(users[i].toString(), ChatUserDto::class.java))
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

    override fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            if (strategic) {
                val data: JSONObject =
                    JSONObject().put("type", "strategic").put("content", message)
                        .put("user", getUser()._id)
                        .put("room", Constants.room)
                socketService?.socketEmit("sendMessage", data)
            } else {
                val data: JSONObject =
                    JSONObject().put("type", "text").put("content", message)
                        .put("user", getUser()._id)
                        .put("room", Constants.room)
                socketService?.socketEmit("sendMessage", data)
            }

            view.setMessageClear()
        } else {
            view.toastMessage("내용을 입력해주세요!")
        }
    }

    override fun getMessages() {
        val arrayList = ArrayList<ChatDto>()
        if (sp.getString("message", null) != null) {
            println("메세지 : " + sp.getString("message", null))
            val jsonArray = JSONArray(sp.getString("message", null))
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
        if (boolean) {
            if (view.getAgreeButtonSelected()) {
                view.setAgreeButtonSelected(false)
                view.setOpposeButtonSelected(false)
            } else {
                view.setAgreeButtonSelected(true)
                view.setOpposeButtonSelected(false)
            }
        } else {
            if (view.getOpposeButtonSelected()) {
                view.setAgreeButtonSelected(false)
                view.setOpposeButtonSelected(false)
            } else {
                view.setAgreeButtonSelected(false)
                view.setOpposeButtonSelected(true)
            }
        }
    }

    override fun plusClick() {
        view.plusDialog()
    }

    override fun checkCameraPermission(num: Int) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {//이전에 이미 권한이 거부되었을 때 설명
                view.toastMessage("권한을 허가해주십시오.")
                setupPermissions()
            } else {//최초로 권한 요청
                makeRequest()
            }
        } else {
            when (num) {
                0 -> view.openGallery()
                1 -> view.openVideo()
            }
        }
    }

    override fun imageGalleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = getPath(currentImageUrl!!)
        val file: File = File(path)
        sendImage(file)
    }

    override fun videoGalleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = getPath(currentImageUrl!!)
        val file: File = File(path)
        sendVideo(file)
    }

    override fun closeCursor() {
        if (c != null) {
            c?.close()
        }
    }

    override fun leave() {
        try {
            val data: JSONObject = JSONObject()
                .put("user", getUser()._id)
                .put("room", Constants.room)
            socketService?.socketEmit("leaveRoom", data)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        reset()
    }

    override fun leaveDialog() {
        view.leaveDialog()
    }

    private fun reset() {
        Constants.opinion = "neutrality"
        Constants.room = null
        spEdit.remove("message")
        spEdit.commit()
        view.finishActivity()
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "onMessage") {
                adapterChatModel?.addItem(
                    gson.fromJson(
                        intent.getStringExtra("onMessageValue"),
                        ChatDto::class.java
                    )
                )
                adapterChatView?.notifyAdapter()
                adapterChatModel?.getItemSize()?.let { view.setChatScrollPosition(it - 1) }
            }

            if (intent?.action == "onRoomInfoChange") {
                val json = JSONObject(intent.getStringExtra("onRoomInfoChangeValue"))
                println("json : $json")
                val users = json.getJSONObject("roomInfo").getJSONArray("users")

                val arrayList = ArrayList<ChatUserDto>()
                for (i in 0 until users.length()) {
                    val user = gson.fromJson(users[i].toString(), ChatUserDto::class.java)
                    if (user._id == getUser()._id) {
                        Constants.opinion = user.opinion.toString()
                    }
                    arrayList.add(user)
                }
                adapterChatUserModel?.clearItems()
                adapterChatUserModel?.addItems(arrayList)
                adapterChatUserView?.notifyAdapter()
            }

            if (intent?.action == "onOfferSubject") {
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
                                JSONObject().put("user", getUser()._id).put("room", Constants.room)
                                    .put("opinion", opinion)
                            socketService?.socketEmit("opinionResult", data)
                            Constants.opinion = opinion
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

            if (intent?.action == "onPresentationOrder") {
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
                            if (Constants.opinion == "agree") {
                                view.setEnable(true)
                            } else {
                                view.setEnable(false)
                            }
                        }
                        "oppose" -> {
                            if (Constants.opinion == "oppose") {
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

                                val data: JSONObject = JSONObject().put("room", Constants.room)
                                    .put("user", getUser()._id)
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

            if (intent?.action == "reVoting") {
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
                                JSONObject().put("user", getUser()._id).put("room", Constants.room)
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

            if (intent?.action == "result") {
                val json = JSONObject(intent.getStringExtra("resultValue"))

                when (json.get("winner").toString()) {
                    "neutrality" -> {
                        view.setResultText("DRAW")
                    }
                    "agree" -> {
                        if (Constants.opinion == "agree") {
                            view.setResultText("WIN")
                        } else {
                            view.setResultText("LOSE")
                        }
                    }
                    "oppose" -> {
                        if (Constants.opinion == "oppose") {
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
                JSONObject().put("room", Constants.room).put("user", getUser()._id)
            socketService?.socketEmit("onMakeRoom", data)
        }
    }

    private fun setupPermissions() {
        //스토리지 읽기 퍼미션을 permission 변수에 담는다
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            100
        )
    }

    private fun getPath(uri: Uri): String? {
        val idx: String = DocumentsContract.getDocumentId(uri).split(":")[1]
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val selection: String = MediaStore.Files.FileColumns._ID + " = " + idx
        c = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            proj,
            selection,
            null,
            null
        )
        val index = c?.getColumnIndexOrThrow(proj[0])
        c?.moveToFirst()
        return index?.let { c?.getString(it) }
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

    private fun sendImage(file: File) {

        if (strategic) {
            try {
                val binaryData: String =
                    Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)

                val data: JSONObject = JSONObject().put("type", "strategic").put("content", "image")
                    .put("user", getUser()._id)
                    .put("room", Constants.room)
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
                    .put("user", getUser()._id)
                    .put("room", Constants.room)
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
                    .put("user", getUser()._id)
                    .put("room", Constants.room)
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
                    .put("user", getUser()._id)
                    .put("room", Constants.room)
                    .put("binaryData", binaryData)
                socketService?.socketEmit("sendMessage", data)
            } catch (e: JSONException) {
                println(e)
            }
        }
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}