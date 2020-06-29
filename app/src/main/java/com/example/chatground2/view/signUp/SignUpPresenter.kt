package com.example.chatground2.view.signUp

import android.content.Context
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.model.Pattern.nicknamePattern
import com.example.chatground2.model.Pattern.passwordPattern
import java.util.regex.Pattern

class SignUpPresenter(
    val context: Context,
    val view: SignUpContract.ISignUpView
) : SignUpContract.ISignUpPresenter, SignUpContract.CallBack {

    private var model: SignUpModel = SignUpModel(context)
    private var toastMessage: ToastMessage = ToastMessage(context)

    private var passEmail: Boolean = false
    private var passNickname: Boolean = false
    private var passPassword: Boolean = false
    private var passPasswordConfirm: Boolean = false

    override fun emptyCheck(
        isEmailEmpty: Boolean,
        isPasswordEmpty: Boolean,
        isPasswordConfirmEmpty: Boolean,
        isNicknameEmpty: Boolean
    ): Boolean {
        when {
            isEmailEmpty -> {
                toastMessage.emailNull()
                return false
            }
            isPasswordEmpty -> {
                toastMessage.passwordNull()
                return false
            }
            isPasswordConfirmEmpty -> {
                toastMessage.passwordConfirmNull()
                return false
            }
            isNicknameEmpty -> {
                toastMessage.nicknameNull()
                return false
            }
        }
        return true
    }

    override fun samePassword(password: String, passwordConfirm: String) {
        passPasswordConfirm = if (password == passwordConfirm) {
            view.setPasswordConfirmCheckAlpha(1.0f)
            true
        } else {
            view.setPasswordConfirmCheckAlpha(0.4f)
            false
        }
    }

    override fun validatedPassword(text: String) {
        passPassword = if (Pattern.matches(passwordPattern, text)) {
            view.setPasswordCheckAlpha(1.0f)
            true
        } else {
            view.setPasswordCheckAlpha(0.4f)
            false
        }
    }

    override fun signUpButtonClick(emailText: String, passwordText: String, nicknameText: String) {
        when {
            !passEmail -> {
                toastMessage.requestEmailOverlap()
            }
            !passPassword -> {
                toastMessage.passwordForm()
            }
            !passPasswordConfirm -> {
                toastMessage.passwordConfirmFail()
            }
            !passNickname -> {
                toastMessage.requestNicknameOverlap()
            }
            else -> {
                val hashMap = HashMap<String, Any>()
                hashMap[emailText] = emailText
                hashMap[passwordText] = passwordText
                hashMap[nicknameText] = nicknameText
                model.signUp(hashMap, this)
            }
        }
    }

    override fun emailOverlapButtonClick(emailText: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            val hashMap = HashMap<String, Any>()
            hashMap[emailText] = emailText
            model.emailOverlap(hashMap, this)
        } else {
            toastMessage.emailForm()
        }
    }

    override fun nicknameOverlapButtonClick(nicknameText: String) {
        if (Pattern.matches(
                nicknamePattern,
                nicknameText
            )
        ) {
            val hashMap = HashMap<String, Any>()
            hashMap[nicknameText] = nicknameText
            model.nicknameOverlap(hashMap, this)
        } else {
            toastMessage.nicknameForm()
        }
    }

    override fun emailChange() {
        passEmail = false
        view.setEmailCheckAlpha(0.4f)
    }

    override fun nicknameChange() {
        passNickname = false
        view.setNicknameCheckAlpha(0.4f)
    }

    override fun onEmailOverlapSuccess() {
        passEmail = true
        toastMessage.emailOverlapPass()
        view.setEmailCheckAlpha(1.0f)
    }

    override fun onEmailOverlapFailure() {
        passEmail = false
        toastMessage.emailOverlapFail()
        view.emailClear()
        view.setEmailCheckAlpha(0.4f)
    }

    override fun onNicknameOverlapSuccess() {
        passNickname = true
        toastMessage.nicknameOverlapPass()
        view.setNicknameCheckAlpha(1.0f)
    }

    override fun onNicknameOverlapFailure() {
        passNickname = false
        toastMessage.nicknameOverlapFail()
        view.nicknameClear()
        view.setNicknameCheckAlpha(0.4f)
    }

    override fun onSignUpSuccess() {
        toastMessage.signUpSuccess()
        view.finishActivity()
    }

    override fun onSignUpFailure() {
        passEmail = false
        passNickname = false
        passPassword = false
        passPasswordConfirm = false
        toastMessage.signUpFailure()
        view.allClear()
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        toastMessage.retrofitError()
    }
}