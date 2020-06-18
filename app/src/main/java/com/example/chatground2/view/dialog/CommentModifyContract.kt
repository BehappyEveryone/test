package com.example.chatground2.view.dialog

import android.content.Intent

interface CommentModifyContract {
    interface ICommentModifyPresenter{
        fun cameraClick()
        fun modifyComment()
        fun cancel()
        fun deleteImage()
        fun onPathCheck(imagePath: String?)
        fun galleryResult(data: Intent?)
        fun closeCursor()
        fun checkCameraPermission()
        fun getIntent(intent: Intent)
    }

    interface ICommentModifyView{
        fun deleteCommentImageDialog()
        fun setCameraImage(path:String?)
        fun progressVisible(boolean: Boolean)
        fun toastMessage(text:String)
        fun finishActivity()
        fun openGallery()
        fun setEnable(boolean: Boolean)
        fun setContentText(text: String)
    }

    interface Listener
    {
        fun onModifyCommentSuccess()
        fun onModifyCommentFailure()
        fun onFailure()
    }
}