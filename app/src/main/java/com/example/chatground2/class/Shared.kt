package com.example.chatground2.`class`

import android.content.Context
import android.content.SharedPreferences
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson

class Shared(val context: Context) {
    val SHARED_PREFERENCE = "ChatGround"

    private val sp: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sp.edit()
    private val gson = Gson()

    fun getMessage(): String? = sp.getString("message", null)

    fun getUser(): UserDto {
        val json = sp.getString("User", "")
        println("user : $json")
        return gson.fromJson(json, UserDto::class.java)
    }

    fun getAuto(): Boolean = sp.getBoolean("Auto", false)
    fun getAutoEmail(): String? = sp.getString("AutoEmail", null)
    fun getAutoPassword(): String? = sp.getString("AutoPassword", null)

    fun setSharedPreference(name: String, value: Any?) {
        when (value) {
            is String, is String? -> {
                editor.putString(name, value as String)
            }
            is Int, is Int? -> {
                editor.putInt(name, value as Int)
            }
            is Boolean, is Boolean? -> {
                editor.putBoolean(name, value as Boolean)
            }
            else -> {
                println("name : $name , save : $value, type : else -> ${value?.javaClass?.name}")
            }
        }
    }

    fun <T> gsonToJson(generic: T): String = gson.toJson(generic)
    fun <T> gsonFromJson(json: String, generic: Class<T>): T = gson.fromJson(json, generic)
    fun editorRemove(name: String): SharedPreferences.Editor = editor.remove(name)
    fun editorClear(): SharedPreferences.Editor = editor.clear()
    fun editorCommit() = editor.commit()//동기
    fun editorApply() = editor.apply()//비동
}