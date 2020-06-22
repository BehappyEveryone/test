package com.example.chatground2.view.writeForum

import android.content.Intent

interface WriteForumContract {
    interface IWriteForumPresenter{
        fun saveClick()
        fun cameraClick()
        fun checkCameraPermission()
        fun galleryResult(data: Intent?)
        fun showImageClick(imageNum: Int)
        fun deleteImage(imageNum: Int)
        fun closeCursor()
    }

    interface IWriteForumView{
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
        fun openGallery()
    }

    interface Listener
    {
        fun onSuccess()
        fun onFailure()
        fun onError(t:Throwable)
    }
}