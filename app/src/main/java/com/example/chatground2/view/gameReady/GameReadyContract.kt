package com.example.chatground2.view.gameReady

interface GameReadyContract {
    interface IGameReadyPresenter{
        fun setBroadCastReceiver()
        fun removeBroadCastReceiver()
        fun bindService()
        fun unbindService()
        fun isSocketConnect(): Boolean?
        fun disconnectSocket()
        fun connectSocket()
    }

    interface IGameReadyView{
        fun setReady()
        fun setMatching()
    }
}