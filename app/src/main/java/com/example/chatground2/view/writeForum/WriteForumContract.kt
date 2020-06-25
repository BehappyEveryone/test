package com.example.chatground2.view.writeForum

import android.content.Intent

interface WriteForumContract {
    interface IWriteForumPresenter{
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

    interface IWriteForumView{
        fun setImage(imagePathList:ArrayList<String>)
        fun createDialog()
        fun createShowImageDialog(imageNum: Int)
        fun progressVisible(boolean: Boolean)
        fun finishActivity()
        fun setEnable(boolean: Boolean)
    }

    interface Listener
    {
        fun onSuccess()
        fun onFailure()
        fun onError(t:Throwable)
    }
}