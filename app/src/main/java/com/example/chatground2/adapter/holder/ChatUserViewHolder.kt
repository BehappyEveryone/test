package com.example.chatground2.adapter.holder

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.model.Constant.agree
import com.example.chatground2.model.Constant.neutrality
import com.example.chatground2.model.Constant.oppose
import com.example.chatground2.model.dto.ChatUserDto
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_drawer.view.*
import java.lang.Exception

class ChatUserViewHolder(
    val context: Context, private val listenerFunc: ((Int) -> Unit)?,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val profile = itemView.CDI_profile
    private val nickname = itemView.CDI_nickname
    private val opinion = itemView.CDI_opinion

    fun onBind(items: ArrayList<ChatUserDto>, position: Int) {
        items[position].let {
            nickname.text = it.nickname

            if (it.profile.isNullOrEmpty()) {
                profile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile_icon))
            } else {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.profile)
                    .into(profile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            profile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.profile_icon))
                        }
                    })
            }

            if (it.opinion == null) {
                opinion.visibility = View.INVISIBLE
            } else {
                opinion.visibility = View.VISIBLE
                it.opinion?.let { op ->
                    when (op) {
                        agree -> {
                            opinion.text = context.getString(R.string.user_opinion_agree)
                            opinion.background =
                                ContextCompat.getDrawable(context, R.drawable.chat_drawer_agree)
                        }
                        oppose -> {
                            opinion.text = context.getString(R.string.user_opinion_oppose)
                            opinion.background =
                                ContextCompat.getDrawable(context, R.drawable.chat_drawer_oppose)
                        }
                        neutrality -> {
                            opinion.text = context.getString(R.string.user_opinion_neutrality)
                            opinion.background =
                                ContextCompat.getDrawable(context, R.drawable.chat_drawer_neutrality)
                        }
                    }
                }
            }
        }
        itemView.setOnClickListener {
            listenerFunc?.invoke(position)
        }
    }
}