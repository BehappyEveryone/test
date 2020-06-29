package com.example.chatground2.view.signUp

import com.example.chatground2.model.dto.UserDto

interface SignUpContract {
    interface ISignUpPresenter{
        fun validatedPassword(text: String)
        fun samePassword(password: String,passwordConfirm:String)
        fun signUpButtonClick(emailText: String, passwordText: String, nicknameText: String)
        fun emailOverlapButtonClick(emailText:String)
        fun nicknameOverlapButtonClick(nicknameText:String)
        fun emailChange()
        fun nicknameChange()
        fun emptyCheck(isEmailEmpty:Boolean,isPasswordEmpty:Boolean,isPasswordConfirmEmpty:Boolean,isNicknameEmpty:Boolean): Boolean
    }

    interface ISignUpView{
        fun nicknameClear()
        fun emailClear()
        fun allClear()
        fun finishActivity()
        fun progressVisible(boolean: Boolean)
        fun setEmailCheckAlpha(float: Float)
        fun setPasswordCheckAlpha(float: Float)
        fun setPasswordConfirmCheckAlpha(float: Float)
        fun setNicknameCheckAlpha(float: Float)
    }

    interface CallBack
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