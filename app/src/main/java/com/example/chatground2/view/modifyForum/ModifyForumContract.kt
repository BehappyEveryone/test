package com.example.chatground2.view.modifyForum

import android.content.Intent
import javax.security.auth.Subject

interface ModifyForumContract {
    interface IModifyForumPresenter{
        fun getIntent(intent: Intent)
        fun saveClick()
        fun cameraClick()
        fun checkCameraPermission()
        fun galleryResult(data: Intent?)
        fun showImageClick(imageNum: Int)
        fun deleteImage(imageNum: Int)
        fun closeCursor()
    }

    interface IModifyForumView{
        fun setImage(imagePathList:ArrayList<String>)
        fun createDialog()
        fun createShowImageDialog(imageNum: Int)
        fun progressVisible(boolean: Boolean)
        fun toastMessage(text:String)
        fun finishActivity()
        fun isTitleEmpty():Boolean
        fun isContentEmpty():Boolean
        fun getTitleText():String
        fun getContentText():String
        fun getSelectSubject():String
        fun setEnable(boolean: Boolean)
        fun setDefault(subject: String,title:String,content:String)
        fun openGallery()
    }

    interface Listener
    {
        fun onModifySuccess()
        fun onModifyFailure()
        fun onError(e:Throwable)
    }
}