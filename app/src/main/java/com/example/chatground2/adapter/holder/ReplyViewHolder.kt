package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.CommentDto
import com.example.chatground2.model.dto.UserDto
import com.example.chatground2.R
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_reply.view.*
import java.lang.Exception
import java.text.DateFormat

class ReplyViewHolder (
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val content = itemView.RI_content
    private val date = itemView.RI_date
    private val nickname = itemView.RI_nickname
    private val image = itemView.RI_image
    private val profile = itemView.RI_profile
    private val modifyButton = itemView.RI_modifyButton
    private val deleteButton = itemView.RI_deleteButton

    fun onBind(
        items: ArrayList<CommentDto>,
        position: Int
    ) {
        items[position].let {
            if(it.user._id == getUser()._id)
            {
                modifyButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
            }

            content.text = it.content
            date.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.birth)
            nickname.text = it.user.nickname

            if (!it.imageUrl.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                Picasso.get()
                    .load(IpAddress.BaseURL + it.imageUrl)
                    .into(image, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            Picasso.get().load(R.drawable.noimage).into(image)
                        }
                    })
            }

            if (!it.user.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user.profile)
                    .into(profile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            Picasso.get().load(R.drawable.default_profile).into(profile)
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