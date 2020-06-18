package com.example.chatground2.view.chatGround

import android.content.Intent
import com.example.chatground2.adapter.adapterContract.ChatAdapterContract
import com.example.chatground2.adapter.adapterContract.ChatUserAdapterContract

interface ChatGroundContract {
    interface IChatGroundPresenter{
        var adapterChatModel: ChatAdapterContract.Model?
        var adapterChatView: ChatAdapterContract.View?
        var adapterChatUserModel: ChatUserAdapterContract.Model?
        var adapterChatUserView: ChatUserAdapterContract.View?
        fun getIntent(intent: Intent)
        fun setBroadCastReceiver()
        fun removeBroadCastReceiver()
        fun bindService()
        fun unbindService()
        fun drawerClick()
        fun sendMessage(message:String)
        fun getMessages()
        fun removeMessages()
        fun setOpinion(boolean: Boolean)
        fun plusClick()
        fun checkCameraPermission(num:Int)
        fun imageGalleryResult(data: Intent?)
        fun videoGalleryResult(data: Intent?)
        fun closeCursor()
        fun leave()
        fun leaveDialog()
    }

    interface IChatGroundView{
        fun openDrawer()
        fun toastMessage(text:String)
        fun finishActivity()
        fun setMessageClear()
        fun setChatScrollPosition(position:Int)
        fun setEnable(boolean: Boolean)
        fun setSubjectText(text: String)
        fun setTimeText(text: String)
        fun setOpinionVisible(boolean: Boolean)
        fun setAgreeButtonSelected(boolean: Boolean)
        fun getAgreeButtonSelected():Boolean
        fun setOpposeButtonSelected(boolean: Boolean)
        fun getOpposeButtonSelected():Boolean
        fun setResultText(text: String)
        fun plusDialog()
        fun leaveDialog()
        fun openGallery()
        fun openVideo()
    }
}