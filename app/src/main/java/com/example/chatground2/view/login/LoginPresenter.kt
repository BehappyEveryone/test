package com.example.chatground2.view.login

import android.content.Context
import com.example.chatground2.`class`.Shared
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto


class LoginPresenter(
    private val context: Context,
    val view: LoginContract.ILoginView
) : LoginContract.ILoginPresenter, LoginContract.Listener {
    private var model: Model = Model(context)
    private var shared:Shared = Shared(context)

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

    override fun signInButtonClick() {
        val email = view.getEmailText()
        val password = view.getPasswordText()

        if (email.isEmpty()) {
            view.toastMessage("이메일을 입력해주세요")
            view.setEmailFocus()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.toastMessage("이메일 형식에 맞게 입력해주세요")
            view.setEmailText("")
            view.setEmailFocus()
            return
        }

        if (password.isEmpty()) {
            view.toastMessage("패스워드를 입력해주세요")
            view.setPasswordFocus()
            return
        }

        val hashMap = HashMap<String, Any>()
        hashMap["email"] = email
        hashMap["password"] = password

        view.setEnable(false)
        model.signIn(hashMap, this)
    }

    override fun onLoginSuccess(userDto: UserDto) {//로그인 할 때 유저 저장
        val userJson = shared.gsonToJson(userDto)
        shared.editorClear()
        shared.setSharedPreference("User", userJson)
        if(view.isAutoLogin()) {
            shared.setSharedPreference("Auto",true)
            shared.setSharedPreference("AutoEmail",view.getEmailText())
            shared.setSharedPreference("AutoPassword",view.getPasswordText())
        }
        shared.editorCommit()
        view.setEnable(true)
        view.toastMessage("로그인 성공")
        view.finishActivity()
        view.enterMainActivity()
    }

    override fun onLoginFailure() {
        view.setEnable(true)
        view.toastMessage("아이디 또는 패스워드가 일치하지 않습니다.")
        view.setEmailText("")
        view.setPasswordText("")
        view.setEmailFocus()
    }

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        view.toastMessage("통신 실패")
    }
}