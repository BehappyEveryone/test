package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_image_left.view.*
import java.lang.Exception
import java.text.DateFormat

class ChatImageLeftViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val cilContent = itemView.CIL_content
    private val cilDate = itemView.CIL_date
    private val cilNickname = itemView.CIL_nickname
    private val cilProfile = itemView.CIL_profile

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            Picasso.get().load(it.content).into(cilContent, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    println("이미지 에러 : $e")
                    cilContent.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.noimage
                        )
                    )
                }
            })
            cilDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
            cilNickname.text = it.user?.nickname
            if (!it.user?.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user?.profile)
                    .into(cilProfile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            cilProfile.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.profile_icon
                                )
                            )
                        }
                    })
            }
        }
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}