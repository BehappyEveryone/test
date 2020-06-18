package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_chat_text_right.view.*
import java.text.DateFormat

class ChatTextRightViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val ctrContent = itemView.CTR_content
    private val ctrDate = itemView.CTR_date


    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            ctrContent.text = it.content
            ctrDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
        }
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}