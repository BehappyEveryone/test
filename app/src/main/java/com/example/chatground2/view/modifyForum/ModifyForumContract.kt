package com.example.chatground2.view.modifyForum

import android.content.Intent
import javax.security.auth.Subject

interface ModifyForumContract {
    interface IModifyForumPresenter{
        fun getIntent(intent: Intent)
        fun saveClick(isTitleEmpty:Boolean,isContentEmpty:Boolean,subject:String,title:String,content:String)
        fun cameraClick()
        fun checkCameraPermission()
        fun galleryResult(data: Intent?)
        fun showImageClick(imageNum: Int)
        fun deleteImage(imageNum: Int)
        fun closeCursor()
        fun deniedPermission()
        fun resultCancel()
    }

    interface IModifyForumView{
        fun finishActivity()
        fun setImage(imagePathList:ArrayList<String>)
        fun createDialog()
        fun createShowImageDialog(imageNum: Int)
        fun progressVisible(boolean: Boolean)
        fun setEnable(boolean: Boolean)
        fun setDefault(subject: String,title:String,content:String)
    }

    interface CallBack
    {
        fun onModifySuccess()
        fun onModifyFailure()
        fun onError(e:Throwable)
    }
}