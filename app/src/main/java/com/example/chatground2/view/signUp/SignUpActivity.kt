package com.example.chatground2.view.signUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.chatground2.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), SignUpContract.ISignUpView, View.OnClickListener {

    private var presenter: SignUpPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initialize()
    }

    private fun initialize()
    {
        presenter = SignUpPresenter(this,this)

        SU_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                presenter?.emailChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        SU_passwordConfirm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                presenter?.samePassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        SU_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                presenter?.validatedPassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        SU_nickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                presenter?.nicknameChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        SU_emailOverlap.setOnClickListener(this)
        SU_nicknameOverlap.setOnClickListener(this)
        SU_signUp.setOnClickListener(this)
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            SU_progressbar.visibility = View.VISIBLE
        } else {
            SU_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun finishActivity() = finish()

    override fun toastMessage(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

    override fun getEmailText(): String = SU_email.text.toString()

    override fun getPasswordText(): String = SU_password.text.toString()

    override fun getPasswordConfirmText(): String = SU_passwordConfirm.text.toString()

    override fun getNicknameText(): String = SU_nickname.text.toString()

    override fun setEmailCheckAlpha(float: Float) {
        SU_emailCheck.alpha = float
    }

    override fun setPasswordCheckAlpha(float: Float) {
        SU_passwordCheck.alpha = float
    }

    override fun setPasswordConfirmCheckAlpha(float: Float) {
        SU_passwordConfirmCheck.alpha = float
    }

    override fun setNicknameCheckAlpha(float: Float) {
        SU_nicknameCheck.alpha = float
    }

    override fun emailClear()
    {
        SU_email.setText("")
    }

    override fun nicknameClear()
    {
        SU_nickname.setText("")
    }

    override fun allClear() {
        SU_email.setText("")
        SU_password.setText("")
        SU_passwordConfirm.setText("")
        SU_nickname.setText("")
        SU_email.requestFocus()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.SU_emailOverlap -> presenter?.emailOverlapButtonClick()
            R.id.SU_nicknameOverlap -> presenter?.nicknameOverlapButtonClick()
            R.id.SU_signUp -> {
                if(presenter?.emptyCheck()!!)
                {
                    presenter?.signUpButtonClick()
                }
            }
        }
    }
}
