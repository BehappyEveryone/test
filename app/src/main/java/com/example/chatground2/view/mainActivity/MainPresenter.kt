package com.example.chatground2.view.mainActivity

import android.content.*
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson

class MainPresenter(
    private val context: Context,
    val view: MainContract.IMainView
) : MainContract.IMainPresenter {
    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}