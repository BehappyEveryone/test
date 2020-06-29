package com.example.chatground2.view.gameReady

import android.content.*
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.chatground2.model.KeyName.socketOnConnect
import com.example.chatground2.model.KeyName.socketOnDisconnect
import com.example.chatground2.service.SocketService
import com.example.chatground2.view.mainActivity.MainContract

class GameReadyPresenter(
    private val context: Context,
    val view: GameReadyContract.IGameReadyView
) : GameReadyContract.IGameReadyPresenter {

    private var socketService: SocketService? = null

    private val intentFilter: IntentFilter = IntentFilter()

    init {
        //받을 브로드캐스트 리시버
        intentFilter.addAction(socketOnConnect)
        intentFilter.addAction(socketOnDisconnect)
    }

    override fun disconnectSocket() {
        socketService?.disconnectSocket()
    }

    override fun connectSocket() {
        socketService?.connectSocket()
    }

    override fun isSocketConnect(): Boolean? = socketService?.isConnect()

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

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == socketOnConnect) {
                view.setMatching()
            }
            if (intent?.action == socketOnDisconnect) {
                view.setReady()
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
        }
    }
}