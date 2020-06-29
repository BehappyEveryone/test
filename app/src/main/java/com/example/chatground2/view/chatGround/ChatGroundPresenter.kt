package com.example.chatground2.view.chatGround

import android.content.*
import android.net.Uri
import android.os.IBinder
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatground2.R
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.adapter.adapterContract.ChatAdapterContract
import com.example.chatground2.adapter.adapterContract.ChatUserAdapterContract
import com.example.chatground2.api.SocketIo
import com.example.chatground2.model.Constant.agree
import com.example.chatground2.model.Constant.all
import com.example.chatground2.model.Constant.binaryText
import com.example.chatground2.model.Constant.contentText
import com.example.chatground2.model.Constant.emitLeaveRoom
import com.example.chatground2.model.Constant.emitOnMakeRoom
import com.example.chatground2.model.Constant.emitOpinionResult
import com.example.chatground2.model.Constant.emitReVoteResult
import com.example.chatground2.model.Constant.emitResultValue
import com.example.chatground2.model.Constant.emitSendMessage
import com.example.chatground2.model.Constant.emitStrategicTimeComplete
import com.example.chatground2.model.Constant.emitStrategicTimeComplete2
import com.example.chatground2.model.Constant.intentMessageValue
import com.example.chatground2.model.Constant.intentOfferSubjectValue
import com.example.chatground2.model.Constant.intentPresentationOrderValue
import com.example.chatground2.model.Constant.intentReVotingValue
import com.example.chatground2.model.Constant.intentRoomInfoChangeValue
import com.example.chatground2.model.Constant.messageText
import com.example.chatground2.model.Constant.neutrality
import com.example.chatground2.model.Constant.opinionText
import com.example.chatground2.model.Constant.oppose
import com.example.chatground2.model.Constant.reVoteText
import com.example.chatground2.model.Constant.roomText
import com.example.chatground2.model.Constant.socketMessage
import com.example.chatground2.model.Constant.socketOfferSubject
import com.example.chatground2.model.Constant.socketPresentationOrder
import com.example.chatground2.model.Constant.socketReVoting
import com.example.chatground2.model.Constant.socketResult
import com.example.chatground2.model.Constant.socketRoomInfoChange
import com.example.chatground2.model.Constant.subjectText
import com.example.chatground2.model.Constant.timeText
import com.example.chatground2.model.Constant.typeImageText
import com.example.chatground2.model.Constant.typeStrategicImageText
import com.example.chatground2.model.Constant.typeStrategicTextText
import com.example.chatground2.model.Constant.typeStrategicVideoText
import com.example.chatground2.model.Constant.typeText
import com.example.chatground2.model.Constant.typeTextText
import com.example.chatground2.model.Constant.typeVideoText
import com.example.chatground2.model.Constant.userText
import com.example.chatground2.model.Constant.usersText
import com.example.chatground2.model.Constant.winnerText
import com.example.chatground2.model.Paths.authorityPath
import com.example.chatground2.model.dto.*
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
    val context: Context,
    val view: ChatGroundContract.IChatGroundView
) : ChatGroundContract.IChatGroundPresenter {

    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery: Gallery = Gallery(context)
    private var toastMessage: ToastMessage = ToastMessage(context)
    private var gson: Gson = Gson()

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
        intentFilter.addAction(socketMessage)
        intentFilter.addAction(socketOfferSubject)
        intentFilter.addAction(socketRoomInfoChange)
        intentFilter.addAction(socketPresentationOrder)
        intentFilter.addAction(socketReVoting)
        intentFilter.addAction(socketResult)
    }

    override fun getIntent(intent: Intent) {
        val users: ArrayList<ChatUserDto> =
            intent.getSerializableExtra(usersText) as ArrayList<ChatUserDto>
        adapterChatUserModel?.addItems(users)
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

    override fun setOpinion(isAgree: Boolean, agreeState: Boolean, opposeState: Boolean) {
        if (isAgree) {//agree 버튼을 눌렀을 때
            if (agreeState) {//agree 버튼이 이미 눌러져 있으면
                view.setAgreeButtonSelected(false)
                SocketIo.opinion = neutrality
            } else {//agree 버튼이 이미 눌러져 있는 상태가 아니라면
                view.setAgreeButtonSelected(true)
                SocketIo.opinion = agree
            }
            view.setOpposeButtonSelected(false)
        } else {
            if (opposeState) {
                view.setOpposeButtonSelected(false)
                SocketIo.opinion = neutrality
            } else {
                view.setOpposeButtonSelected(true)
                SocketIo.opinion = oppose
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
        sendFile(file, 0)
    }

    override fun videoGalleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        val file: File = File(path)
        sendFile(file, 1)
    }

    override fun leave() {
        try {
            val data: JSONObject = JSONObject()
                .put(userText, shared.getUser()._id)
                .put(roomText, SocketIo.room)
            socketService?.socketEmit(emitLeaveRoom, data)
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
                    0 -> gallery.openGallery()
                    1 -> gallery.openVideo()
                }
            }
            1 -> {//이전에 이미 권한이 거부됨
                toastMessage.requestPermission()
                permission.setupPermissions()
            }
            3 -> {//최초로 권한 요청
                permission.makeRequest()
            }
        }
    }

    override fun deniedPermission() {
        toastMessage.deniedPermission()
    }

    override fun resultCancel() {
        toastMessage.resultCancel()
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    private fun reset() {
        SocketIo.opinion = neutrality
        SocketIo.room = null
        shared.editorRemove(messageText)
        shared.editorCommit()
        view.finishActivity()
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                socketMessage -> {
                    val message: ChatDto? = intent.getParcelableExtra(intentMessageValue)
                    message?.let { adapterChatModel?.addItem(it) }
                    adapterChatView?.notifyAdapter()
                    adapterChatModel?.getItemSize()?.let { view.setChatScrollPosition(it - 1) }
                }
                socketRoomInfoChange -> {
                    val roomInfo: ChatRoomInfoDto =
                        intent.getParcelableExtra(intentRoomInfoChangeValue)
                    val users: ArrayList<ChatUserDto> = roomInfo.users
                    users.forEach { user ->
                        user.opinion?.let {
                            if (user._id == shared.getUser()._id) {
                                SocketIo.opinion = it
                            }
                        }
                    }

                    adapterChatUserModel?.clearItems()
                    adapterChatUserModel?.addItems(users)
                    adapterChatUserView?.notifyAdapter()
                }
                socketOfferSubject -> {
                    val offerSubject: ChatOfferSubjectDto =
                        intent.getParcelableExtra(intentOfferSubjectValue)

                    CoroutineScope(Dispatchers.Main).launch {
                        view.setEnable(false)

                        for (i: Int in 0..offerSubject.subject.length) {
                            view.setSubjectText(offerSubject.subject.substring(0, i))
                            delay(100)
                        }

                        view.setOpinionVisible(true)
                        for (i: Int in offerSubject.time / 1000 downTo 0) {
                            if (i == 0) {
                                view.setOpinionVisible(false)
                                val opinion: String = SocketIo.opinion

                                val data: JSONObject =
                                    JSONObject().put(userText, shared.getUser()._id)
                                        .put(roomText, SocketIo.room)
                                        .put(opinionText, opinion)
                                socketService?.socketEmit(emitOpinionResult, data)
                                view.setAgreeButtonSelected(false)
                                view.setOpposeButtonSelected(false)
                            }
                            if (i < 10) {
                                context?.getString(R.string.chat_ground_dynamic_time_second1, i)
                                    ?.let {
                                        view.setTimeText(it)
                                    }
                            } else {
                                context?.getString(R.string.chat_ground_dynamic_time_second2, i)
                                    ?.let {
                                        view.setTimeText(it)
                                    }
                            }
                            delay(1000)//1000
                        }
                    }
                }
                socketPresentationOrder -> {
                    val order: ChatSystemOrderDto =
                        intent.getParcelableExtra(intentPresentationOrderValue)

                    CoroutineScope(Dispatchers.Main).launch {
                        if (order.order == emitStrategicTimeComplete || order.order == emitStrategicTimeComplete2) {
                            strategic = true
                        }
                        when (order.speaking) {
                            all -> {
                                view.setEnable(true)
                            }
                            agree -> {
                                if (SocketIo.opinion == agree) {
                                    view.setEnable(true)
                                } else {
                                    view.setEnable(false)
                                }
                            }
                            oppose -> {
                                if (SocketIo.opinion == oppose) {
                                    view.setEnable(true)
                                } else {
                                    view.setEnable(false)
                                }
                            }
                        }

                        for (i: Int in order.time / 1000 downTo 0) {
                            val sec: Int = i % 60
                            val min: Int = i / 60
                            when {
                                min == 0 && sec == 0 -> {
                                    view.setEnable(false)
                                    context?.getString(R.string.chat_ground_time)?.let {
                                        view.setTimeText(it)
                                    }
                                    if (strategic) {
                                        strategic = false
                                    }

                                    val data: JSONObject = JSONObject().put(roomText, SocketIo.room)
                                        .put(userText, shared.getUser()._id)
                                    socketService?.socketEmit(order.order, data)
                                }
                                min == 0 -> when {
                                    sec < 10 -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_second1,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                    else -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_second2,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                }
                                min < 10 -> when {
                                    sec < 10 -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_minute1,
                                            min,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                    else -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_minute2,
                                            min,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                }
                                else -> when {
                                    sec < 10 -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_minute3,
                                            min,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                    else -> {
                                        context?.getString(
                                            R.string.chat_ground_dynamic_time_minute4,
                                            min,
                                            sec
                                        )?.let {
                                            view.setTimeText(it)
                                        }
                                    }
                                }
                            }
                            delay(1000)
                        }
                    }
                }
                socketReVoting -> {
                    val time = intent.getIntExtra(intentReVotingValue,0)

                    CoroutineScope(Dispatchers.Main).launch {
                        view.setEnable(false)

                        view.setOpinionVisible(true)
                        for (i: Int in time / 1000 downTo 0) {
                            if (i == 0) {
                                view.setOpinionVisible(false)

                                val reVote: String = SocketIo.opinion

                                val data: JSONObject =
                                    JSONObject().put(userText, shared.getUser()._id)
                                        .put(roomText, SocketIo.room)
                                        .put(reVoteText, reVote)
                                socketService?.socketEmit(emitReVoteResult, data)
                                view.setAgreeButtonSelected(false)
                                view.setOpposeButtonSelected(false)
                            }
                            if (i < 10) {
                                context?.getString(R.string.chat_ground_dynamic_time_second1, i)
                                    ?.let {
                                        view.setTimeText(it)
                                    }
                            } else {
                                context?.getString(R.string.chat_ground_dynamic_time_second2, i)
                                    ?.let {
                                        view.setTimeText(it)
                                    }
                            }
                            delay(1000)//1000
                        }
                    }
                }

                socketResult -> {
                    val winner = intent.getStringExtra(emitResultValue)

                    when (winnerText) {
                        neutrality -> {
                            context?.getString(R.string.chat_ground_draw)?.let {
                                view.setResultText(it)
                            }
                        }
                        agree -> {
                            if (SocketIo.opinion == agree) {
                                context?.getString(R.string.chat_ground_win)?.let {
                                    view.setResultText(it)
                                }
                            } else {
                                context?.getString(R.string.chat_ground_lose)?.let {
                                    view.setResultText(it)
                                }
                            }
                        }
                        oppose -> {
                            if (SocketIo.opinion == oppose) {
                                context?.getString(R.string.chat_ground_win)?.let {
                                    view.setResultText(it)
                                }
                            } else {
                                context?.getString(R.string.chat_ground_lose)?.let {
                                    view.setResultText(it)
                                }
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
            socketService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val mBinder = service as SocketService.SocketBinder
            socketService = mBinder.getService()

            val data: JSONObject =
                JSONObject().put(roomText, SocketIo.room).put(userText, shared.getUser()._id)
            socketService?.socketEmit(emitOnMakeRoom, data)
        }
    }

    override fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            val data: JSONObject =
                JSONObject().put(contentText, message)
                    .put(userText, shared.getUser()._id)
                    .put(roomText, SocketIo.room)
            if (strategic) {
                data.put(typeText, typeStrategicTextText)
            } else {
                data.put(typeText, typeTextText)
            }
            socketService?.socketEmit(emitSendMessage, data)
            view.setMessageClear()
        } else {
            toastMessage.textNull()
        }
    }

    private fun sendFile(file: File, type: Int) {// type0 이미지 type1 비디오
        val binaryData: String = Base64.encodeToString(convertFileToByte(file), Base64.DEFAULT)
        val data: JSONObject = JSONObject().put(userText, shared.getUser()._id)
            .put(roomText, SocketIo.room)
            .put(binaryText, binaryData)

        if (strategic) {
            when (type) {
                0 -> {
                    data.put(typeText, typeStrategicImageText)
                    data.put(contentText, typeImageText)
                }
                1 -> {
                    data.put(typeText, typeStrategicVideoText)
                    data.put(contentText, typeVideoText)
                }
            }
        } else {
            when (type) {
                0 -> {
                    data.put(typeText, typeImageText)
                    data.put(contentText, typeImageText)
                }
                1 -> {
                    data.put(typeText, typeVideoText)
                    data.put(contentText, typeVideoText)
                }
            }
        }

        socketService?.socketEmit(emitSendMessage, data)
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

    private fun clickVideo(position: Int) {
        try {
            val videoFile = File(adapterChatModel?.getItem(position)?.content)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                authorityPath,
                videoFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "video/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            toastMessage.filePathNull()
        }
    }
}