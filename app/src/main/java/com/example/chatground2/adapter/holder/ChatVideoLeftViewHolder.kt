package com.example.chatground2.adapter.holder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.model.dto.ChatDto
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_video_left.view.*
import java.lang.Exception
import java.text.DateFormat

class ChatVideoLeftViewHolder(
    val context: Context,
    itemView: View,
    private val onVideoClick: ((Int) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    private val cvlContent = itemView.CVL_content
    private val cvlDate = itemView.CVL_date
    private val cvlNickname = itemView.CVL_nickname
    private val cvlProfile = itemView.CVL_profile

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            try {
                val bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(
                    it.content,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                )
                val ob: BitmapDrawable = BitmapDrawable(context.resources, bitmap)
                cvlContent.background = ob

            } catch (e: Exception) {
                e.printStackTrace()
                cvlContent.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.noimage
                )
            }

            cvlContent.setOnClickListener{
                onVideoClick?.invoke(position)
            }

            cvlDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
            cvlNickname.text = it.user?.nickname
            if (!it.user?.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user?.profile)
                    .into(cvlProfile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            cvlProfile.setImageDrawable(
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
}