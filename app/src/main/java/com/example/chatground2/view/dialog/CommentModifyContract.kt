package com.example.chatground2.view.dialog

import android.content.Intent

interface CommentModifyContract {
    interface ICommentModifyPresenter{
        fun cameraClick()
        fun modifyComment(isContentEmpty:Boolean,content:String)
        fun cancel()
        fun deleteImage()
        fun onPathCheck(imagePath: String?)
        fun galleryResult(data: Intent?)
        fun closeCursor()
        fun checkCameraPermission()
        fun getIntent(intent: Intent)
        fun deniedPermission()
        fun resultCancel()
    }

    interface ICommentModifyView{
        fun deleteCommentImageDialog()
        fun setCameraImage(path:String?)
        fun progressVisible(boolean: Boolean)
        fun setEnable(boolean: Boolean)
        fun setContentText(text: String)
        fun finishActivity()
    }

    interface CallBack
    {
        fun onModifyCommentSuccess()
        fun onModifyCommentFailure()
        fun onError(t:Throwable)
    }
}