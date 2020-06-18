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
import kotlinx.android.synthetic.main.item_chat_strategic_left.view.*
import java.lang.Exception
import java.text.DateFormat

class ChatStrategicLeftViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val cslContent = itemView.CSL_content
    private val cslDate = itemView.CSL_date
    private val cslNickname = itemView.CSL_nickname
    private val cslProfile = itemView.CSL_profile

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            cslContent.text = it.content
            when (it.user?.opinion) {
                "agree" -> cslContent.setTextColor(ContextCompat.getColor(context, R.color.blue))
                "oppose" -> cslContent.setTextColor(ContextCompat.getColor(context, R.color.red))
                "neutrality" -> cslContent.setTextColor(ContextCompat.getColor(context, R.color.silver))
            }
            cslDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
            cslNickname.text = it.user?.nickname
            if (!it.user?.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user?.profile)
                    .into(cslProfile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            cslProfile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile_icon))
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