package com.example.chatground2.view.profile

import android.content.Intent
import com.example.chatground2.model.dto.UserDto

interface ProfileContract {
    interface IProfilePresenter{
        fun saveProfile(introduce: String)
        fun callUser()
        fun logout()
        fun logoutClick()
        fun profileImageClick()
        fun profileInit()
        fun galleryResult(data: Intent?)
        fun checkCameraPermission()
        fun closeCursor()
        fun defaultImage()
        fun deniedPermission()
        fun resultCancel()
    }

    interface IProfileView{
        fun setEmail(text: String)
        fun setNickname(text: String)
        fun setIntroduce(text: String)
        fun setProfileImage(path:String?)
        fun finishActivity()
        fun enterLoginActivity()
        fun progressVisible(boolean: Boolean)
        fun logoutDialog()
        fun imageDialog()
        fun setEnable(boolean: Boolean)
    }

    interface CallBack
    {
        fun onCallUserSuccess(user:UserDto)
        fun onCallUserFailure()
        fun onSaveSuccess()
        fun onSaveFailure()
        fun onError(t:Throwable)
    }
}