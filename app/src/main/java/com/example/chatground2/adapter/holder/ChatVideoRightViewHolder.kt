package com.example.chatground2.adapter.holder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.model.dto.ChatDto
import kotlinx.android.synthetic.main.item_chat_video_right.view.*
import java.io.File
import java.lang.Exception
import java.text.DateFormat

class ChatVideoRightViewHolder(
    val context: Context,
    itemView: View,
    private val onVideoClick: ((Int) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    private val cvrContent = itemView.CVR_content
    private val cvrDate = itemView.CVR_date

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            try {
                val file = File(it.content)
                val bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(
                    file.path,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                )
                val ob: BitmapDrawable = BitmapDrawable(context.resources, bitmap)
                cvrContent.background = ob

            } catch (e: Exception) {
                e.printStackTrace()
                cvrContent.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.noimage
                )
            }

            cvrContent.setOnClickListener{
                onVideoClick?.invoke(position)
            }

            cvrDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
        }
    }
}