package com.example.chatground2.`class`

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.chatground2.R

class ToastMessage(val context: Context) {
    fun toastMessage(text: String) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    fun deniedPermission() = Toast.makeText(context, context.getText(R.string.denied_permission), Toast.LENGTH_LONG).show()
    fun requestPermission() = Toast.makeText(context, context.getText(R.string.request_permission), Toast.LENGTH_LONG).show()
    fun resultCancel() = Toast.makeText(context, context.getText(R.string.activity_result_cancel), Toast.LENGTH_LONG).show()
    fun textNull() = Toast.makeText(context, context.getText(R.string.text_null), Toast.LENGTH_LONG).show()
    fun filePathNull() = Toast.makeText(context, context.getText(R.string.file_path_null), Toast.LENGTH_LONG).show()
    fun forumPathNull() = Toast.makeText(context, context.getText(R.string.forum_path_null), Toast.LENGTH_LONG).show()
    fun retrofitSuccess() = Toast.makeText(context, context.getText(R.string.retrofit_success), Toast.LENGTH_LONG).show()
    fun retrofitFailure() = Toast.makeText(context, context.getText(R.string.retrofit_failure), Toast.LENGTH_LONG).show()
    fun retrofitError() = Toast.makeText(context, context.getText(R.string.retrofit_error), Toast.LENGTH_LONG).show()
    fun forumLast() = Toast.makeText(context, context.getText(R.string.forum_last_page_message), Toast.LENGTH_LONG).show()
    fun loginSuccess() = Toast.makeText(context, context.getText(R.string.login_success_message), Toast.LENGTH_LONG).show()
    fun loginFailure() = Toast.makeText(context, context.getText(R.string.login_failure_message), Toast.LENGTH_LONG).show()
    fun emailNull() = Toast.makeText(context, context.getText(R.string.email_null), Toast.LENGTH_LONG).show()
    fun passwordNull() = Toast.makeText(context, context.getText(R.string.password_null), Toast.LENGTH_LONG).show()
    fun emailForm() = Toast.makeText(context, context.getText(R.string.email_form), Toast.LENGTH_LONG).show()
    fun imageOver() = Toast.makeText(context, context.getText(R.string.image_over_message), Toast.LENGTH_LONG).show()

    fun titleNull() = Toast.makeText(context, context.getText(R.string.forum_title_null), Toast.LENGTH_LONG).show()
    fun contentNull() = Toast.makeText(context, context.getText(R.string.forum_content_null), Toast.LENGTH_LONG).show()
    fun requestEmailOverlap() = Toast.makeText(context, context.getText(R.string.request_email_overlap), Toast.LENGTH_LONG).show()
    fun requestNicknameOverlap() = Toast.makeText(context, context.getText(R.string.request_nickname_overlap), Toast.LENGTH_LONG).show()
    fun passwordForm() = Toast.makeText(context, context.getText(R.string.password_form), Toast.LENGTH_LONG).show()
    fun nicknameForm() = Toast.makeText(context, context.getText(R.string.nickname_form), Toast.LENGTH_LONG).show()
    fun passwordConfirmFail() = Toast.makeText(context, context.getText(R.string.password_confirm_fail), Toast.LENGTH_LONG).show()
    fun passwordConfirmNull() = Toast.makeText(context, context.getText(R.string.password_confirm_null), Toast.LENGTH_LONG).show()
    fun nicknameNull() = Toast.makeText(context, context.getText(R.string.nickname_null), Toast.LENGTH_LONG).show()
    fun nicknameOverlapPass() = Toast.makeText(context, context.getText(R.string.nickname_overlap_pass), Toast.LENGTH_LONG).show()
    fun nicknameOverlapFail() = Toast.makeText(context, context.getText(R.string.nickname_overlap_fail), Toast.LENGTH_LONG).show()
    fun emailOverlapPass() = Toast.makeText(context, context.getText(R.string.email_overlap_pass), Toast.LENGTH_LONG).show()
    fun emailOverlapFail() = Toast.makeText(context, context.getText(R.string.email_overlap_fail), Toast.LENGTH_LONG).show()
    fun signUpSuccess() = Toast.makeText(context, context.getText(R.string.sign_up_success), Toast.LENGTH_LONG).show()
    fun signUpFailure() = Toast.makeText(context, context.getText(R.string.sign_up_failure), Toast.LENGTH_LONG).show()

    fun callUserFailure() = Toast.makeText(context, context.getText(R.string.call_user_failure), Toast.LENGTH_LONG).show()
    fun saveSuccess() = Toast.makeText(context, context.getText(R.string.save_success), Toast.LENGTH_LONG).show()
    fun saveFailure() = Toast.makeText(context, context.getText(R.string.save_failure), Toast.LENGTH_LONG).show()
}