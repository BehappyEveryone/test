package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.model.RequestCode
import com.example.chatground2.model.dto.ChatDto
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_text_left.view.*
import java.lang.Exception
import java.text.DateFormat

class ChatTextLeftViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val ctlContent = itemView.CTL_content
    private val ctlDate = itemView.CTL_date
    private val ctlNickname = itemView.CTL_nickname
    private val ctlProfile = itemView.CTL_profile

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            ctlContent.text = it.content
            ctlDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
            ctlNickname.text = it.user?.nickname
            if (!it.user?.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user?.profile)
                    .into(ctlProfile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            ctlProfile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile_icon))
                        }
                    })
            }
        }
    }
}