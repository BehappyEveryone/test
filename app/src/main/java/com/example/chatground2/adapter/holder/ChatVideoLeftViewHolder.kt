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
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dto.ChatDto
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat_video_left.view.*
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.text.DateFormat

class ChatVideoLeftViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

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

            cvlContent.setOnClickListener { _ ->
                try {
                    val videoFile = File(it.content)
                    val uri:Uri = FileProvider.getUriForFile(context, "com.example.chatground2.fileprovider", videoFile)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "video/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//DO NOT FORGET THIS EVER
                    context.startActivity(intent)
                }catch (e: FileNotFoundException){
                    e.printStackTrace()
                    Toast.makeText(context, "파일을 찾을 수 없습니다", Toast.LENGTH_LONG).show()
                }
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

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}