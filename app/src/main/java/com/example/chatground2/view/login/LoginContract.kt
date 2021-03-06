package com.example.chatground2.view.login

import com.example.chatground2.model.dto.UserDto

interface LoginContract {
    interface ILoginPresenter{
        fun signUpButtonClick()
        fun signInButtonClick(email:String,password:String)
        fun autoLogin()
    }

    interface ILoginView{
        fun finishActivity()
        fun progressVisible(boolean: Boolean)
        fun enterMainActivity()
        fun enterSignUpActivity()
        fun setEmailText(text: String?)
        fun setPasswordText(text: String?)
        fun setEmailFocus()
        fun setPasswordFocus()
        fun setEnable(boolean: Boolean)
        fun isAutoLogin():Boolean
        fun clickSignIn():Boolean
    }

    interface CallBack
    {
        fun onLoginSuccess(userDto: UserDto)
        fun onLoginFailure()
        fun onError(t:Throwable)
    }
}