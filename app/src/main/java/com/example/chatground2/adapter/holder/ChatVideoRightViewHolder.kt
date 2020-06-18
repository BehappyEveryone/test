package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_chat_video_right.view.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.text.DateFormat

class ChatVideoRightViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

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

            cvrContent.setOnClickListener { _ ->
                try {
                    val videoFile = File(it.content)
                    val uri:Uri = FileProvider.getUriForFile(context, "com.example.chatground2.fileprovider", videoFile)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "video/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//DO NOT FORGET THIS EVER
                    context.startActivity(intent)
                }catch (e:FileNotFoundException){
                    e.printStackTrace()
                    Toast.makeText(context, "파일을 찾을 수 없습니다", Toast.LENGTH_LONG).show()
                }
            }

            cvrDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
        }
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}