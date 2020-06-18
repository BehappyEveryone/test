package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_image_right.view.*
import java.lang.Exception
import java.text.DateFormat

class ChatImageRightViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val cirContent = itemView.CIR_content
    private val cirDate = itemView.CIR_date


    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            Picasso.get().load(it.content).into(cirContent, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    println("이미지 에러 : $e")
                    cirContent.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.noimage
                        )
                    )
                }
            })
            cirDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
        }
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}