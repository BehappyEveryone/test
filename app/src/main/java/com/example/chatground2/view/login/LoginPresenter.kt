package com.example.chatground2.view.login

import android.content.Context
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.`class`.Shared
import com.example.chatground2.model.KeyName.autoBoolean
import com.example.chatground2.model.KeyName.autoEmail
import com.example.chatground2.model.KeyName.autoPassword
import com.example.chatground2.model.KeyName.emailText
import com.example.chatground2.model.KeyName.passwordText
import com.example.chatground2.model.KeyName.userCapital
import com.example.chatground2.model.dto.UserDto


class LoginPresenter(
    val context: Context,
    val view: LoginContract.ILoginView
) : LoginContract.ILoginPresenter, LoginContract.CallBack {
    private var model: LoginModel = LoginModel(context)
    private var shared:Shared = Shared(context)
    private var toastMessage:ToastMessage = ToastMessage(context)

    override fun autoLogin() {
        if(shared.getAuto()){
            view.setEmailText(shared.getAutoEmail())
            view.setPasswordText(shared.getAutoPassword())
            view.clickSignIn()
        }
    }

    override fun signUpButtonClick() {
        view.enterSignUpActivity()
    }

    override fun signInButtonClick(email:String,password: String) {
        if (email.isEmpty()) {
            toastMessage.emailNull()
            view.setEmailFocus()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toastMessage.emailForm()
            view.setEmailText("")
            view.setEmailFocus()
            return
        }

        if (password.isEmpty()) {
            toastMessage.passwordNull()
            view.setPasswordFocus()
            return
        }

        val hashMap = HashMap<String, Any>()
        hashMap[emailText] = email
        hashMap[passwordText] = password

        view.setEnable(false)
        model.signIn(hashMap, this)
    }

    override fun onLoginSuccess(userDto: UserDto) {//로그인 할 때 유저 저장
        val email = userDto.email
        val password = userDto.password
        val userJson = shared.gsonToJson(userDto)
        shared.editorClear()
        shared.setSharedPreference(userCapital, userJson)
        if(view.isAutoLogin()) {
            shared.setSharedPreference(autoBoolean,true)
            shared.setSharedPreference(autoEmail,email)
            shared.setSharedPreference(autoPassword,password)
        }
        shared.editorCommit()
        view.setEnable(true)
        toastMessage.loginSuccess()
        view.finishActivity()
        view.enterMainActivity()
    }

    override fun onLoginFailure() {
        view.setEnable(true)
        toastMessage.loginFailure()
        view.setEmailText("")
        view.setPasswordText("")
        view.setEmailFocus()
    }

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        toastMessage.retrofitError()
    }
}