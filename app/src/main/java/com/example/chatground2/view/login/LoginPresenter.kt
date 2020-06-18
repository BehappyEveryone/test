package com.example.chatground2.view.login

import android.content.Context
import android.content.SharedPreferences
import com.example.chatground2.model.Constants.SHARED_PREFERENCE
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson


class LoginPresenter(
    private val context: Context,
    val view: LoginContract.ILoginView
) : LoginContract.ILoginPresenter, LoginContract.Listener {
    private var model: Model = Model(context)

    private val sp: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val spEdit: SharedPreferences.Editor = sp.edit()
    private val gson = Gson()

    override fun autoLogin() {
        if(sp.getBoolean("Auto",false)){
            view.setEmailText(sp.getString("AutoEmail",null))
            view.setPasswordText(sp.getString("AutoPassword",null))
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
        val userJson = gson.toJson(userDto)
        spEdit.clear()
        spEdit.putString("User", userJson)
        if(view.isAutoLogin()) {
            spEdit.putBoolean("Auto", true)
            spEdit.putString("AutoEmail",view.getEmailText())
            spEdit.putString("AutoPassword",view.getPasswordText())
        }
        spEdit.commit()
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