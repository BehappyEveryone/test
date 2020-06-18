package com.example.chatground2.view.signUp

import com.example.chatground2.model.dto.UserDto

interface SignUpContract {
    interface ISignUpPresenter{
        fun validatedPassword(text: String)
        fun samePassword(text: String)
        fun signUpButtonClick()
        fun emailOverlapButtonClick()
        fun nicknameOverlapButtonClick()
        fun emailChange()
        fun nicknameChange()
    }

    interface ISignUpView{
        fun nicknameClear()
        fun emailClear()
        fun allClear()
        fun finishActivity()
        fun progressVisible(boolean: Boolean)
        fun toastMessage(text:String)
        fun getEmailText():String
        fun getPasswordText():String
        fun getPasswordConfirmText():String
        fun getNicknameText():String
        fun setEmailCheckAlpha(float: Float)
        fun setPasswordCheckAlpha(float: Float)
        fun setPasswordConfirmCheckAlpha(float: Float)
        fun setNicknameCheckAlpha(float: Float)
    }

    interface Listener
    {
        fun onEmailOverlapSuccess()
        fun onEmailOverlapFailure()
        fun onNicknameOverlapSuccess()
        fun onNicknameOverlapFailure()
        fun onSignUpSuccess()
        fun onSignUpFailure()
        fun onError(t:Throwable)
    }
}