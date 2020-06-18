package com.example.chatground2.view.signUp

import android.content.Context
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import java.util.regex.Pattern

class SignUpPresenter(
    private val context: Context,
    val view: SignUpContract.ISignUpView
) : SignUpContract.ISignUpPresenter, SignUpContract.Listener {

    private var model: Model = Model(context)

    private var passEmail: Boolean = false
    private var passNickname: Boolean = false
    private var passPassword: Boolean = false
    private var passPasswordConfirm: Boolean = false

    fun emptyCheck(): Boolean {
        when {
            view.getEmailText().isEmpty() -> {
                view.toastMessage("이메일을 입력하세요")
                return false
            }
            view.getPasswordText().isEmpty() -> {
                view.toastMessage("비밀번호를 압력하세요")
                return false
            }
            view.getPasswordConfirmText().isEmpty() -> {
                view.toastMessage("비밀번호 확인을 입력하세요")
                return false
            }
            view.getNicknameText().isEmpty() -> {
                view.toastMessage("닉네임을 입력하세요")
                return false
            }
            else -> return true
        }
    }

    override fun samePassword(text: String) {
        passPasswordConfirm = if (text == view.getPasswordText()) {
            view.setPasswordConfirmCheckAlpha(1.0f)
            true
        } else {
            view.setPasswordConfirmCheckAlpha(0.4f)
            false
        }
    }

    override fun validatedPassword(text: String) {
        passPassword = if (Pattern.matches(
                "^(?=.*\\d{1,20})(?=.*[a-zA-Z]{1,20}).{8,20}$",//영문 최소 1개이상, 숫자 최소 1개 이상, 8~20자리
                text
            )
        ) {
            view.setPasswordCheckAlpha(1.0f)
            true
        } else {
            view.setPasswordCheckAlpha(0.4f)
            false
        }
    }

    override fun signUpButtonClick() {
        when {
            !passEmail -> {
                view.toastMessage("이메일 중복검사를 해주세요.")
            }
            !passPassword -> {
                view.toastMessage("비밀번호가 틀립니다.")
            }
            !passPasswordConfirm -> {
                view.toastMessage("비밀번호가 일치하지 않습니다")
            }
            !passNickname -> {
                view.toastMessage("닉네임 중복검사를 해주세요")
            }
            else -> {
                val hashMap = HashMap<String, Any>()
                hashMap["email"] = view.getEmailText()
                hashMap["password"] = view.getPasswordText()
                hashMap["nickname"] = view.getNicknameText()
                model.signUp(hashMap, this)
            }
        }
    }

    override fun emailOverlapButtonClick() {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(view.getEmailText()).matches()) {
            val hashMap = HashMap<String, Any>()
            hashMap["email"] = view.getEmailText()
            model.emailOverlap(hashMap, this)
        } else {
            view.toastMessage("이메일 형식에 맞게 입력해주세요")
        }
    }

    override fun nicknameOverlapButtonClick() {
        if (Pattern.matches(
                "^[a-zA-Z0-9가-힣]{4,12}$",//영문,한글,숫자 아무렇게나 4~12자리
                view.getPasswordText()
            )
        ) {
            val hashMap = HashMap<String, Any>()
            hashMap["nickname"] = view.getNicknameText()
            model.nicknameOverlap(hashMap, this)
        } else {
            view.toastMessage("닉네임 형식에 맞게 입력해주세요")
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
        view.toastMessage("사용할 수 있는 이메일입니다")
        view.setEmailCheckAlpha(1.0f)
    }

    override fun onEmailOverlapFailure() {
        passEmail = false
        view.toastMessage("이미 존재하는 이메일입니다")
        view.emailClear()
        view.setEmailCheckAlpha(0.4f)
    }

    override fun onNicknameOverlapSuccess() {
        passNickname = true
        view.toastMessage("사용할 수 있는 닉네임입니다")
        view.setNicknameCheckAlpha(1.0f)
    }

    override fun onNicknameOverlapFailure() {
        passNickname = false
        view.toastMessage("이미 존재하는 닉네임입니다")
        view.nicknameClear()
        view.setNicknameCheckAlpha(0.4f)
    }

    override fun onSignUpSuccess() {
        view.toastMessage("가입 성공")
        view.finishActivity()
    }

    override fun onSignUpFailure() {
        passEmail = false
        passNickname = false
        passPassword = false
        passPasswordConfirm = false
        view.toastMessage("가입 실패")
        view.allClear()
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        view.toastMessage("통신 실패")
    }
}