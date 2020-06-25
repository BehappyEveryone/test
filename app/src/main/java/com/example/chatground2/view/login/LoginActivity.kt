package com.example.chatground2.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chatground2.R
import com.example.chatground2.view.mainActivity.MainActivity
import com.example.chatground2.view.signUp.SignUpActivity
import kotlinx.android.synthetic.main.activity_detail_forum.*
import kotlinx.android.synthetic.main.activity_detail_forum.backButton
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_write_forum.*

class LoginActivity : AppCompatActivity(), LoginContract.ILoginView, View.OnClickListener {

    private var presenter: LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialize()
        presenter?.autoLogin()
    }

    private fun initialize()
    {
        presenter = LoginPresenter(this,this)

        L_signIn.setOnClickListener(this)
        L_signUp.setOnClickListener(this)
    }

    override fun progressVisible(boolean: Boolean) {
        if (boolean) {
            L_progressbar.visibility = View.VISIBLE
        } else {
            L_progressbar.visibility = View.INVISIBLE
        }
    }

    override fun setEnable(boolean: Boolean) {
        L_signIn.isEnabled = boolean
        L_signUp.isEnabled = boolean
        L_password.isEnabled = boolean
        L_email.isEnabled = boolean
        L_autoLogin.isEnabled = boolean
    }

    override fun isAutoLogin(): Boolean = L_autoLogin.isChecked

    override fun finishActivity() = finish()

    override fun enterMainActivity() = startActivity(Intent(this, MainActivity::class.java))

    override fun enterSignUpActivity() = startActivity(Intent(this, SignUpActivity::class.java))

    override fun setEmailText(text: String?) = L_email.setText(text)

    override fun setPasswordText(text: String?) = L_password.setText(text)

    override fun clickSignIn():Boolean = L_signIn.callOnClick()

    override fun setEmailFocus(){
        L_email.requestFocus()
    }

    override fun setPasswordFocus() {
        L_password.requestFocus()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.L_signIn -> presenter?.signInButtonClick(L_email.text.toString(),L_password.text.toString())
            R.id.L_signUp -> presenter?.signUpButtonClick()
        }
    }
}
