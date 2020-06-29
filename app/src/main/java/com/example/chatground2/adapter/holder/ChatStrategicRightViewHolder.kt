package com.example.chatground2.adapter.holder

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.model.KeyName
import com.example.chatground2.model.KeyName.agree
import com.example.chatground2.model.KeyName.neutrality
import com.example.chatground2.model.KeyName.oppose
import com.example.chatground2.model.RequestCode
import com.example.chatground2.model.dto.ChatDto
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_chat_strategic_right.view.*
import java.text.DateFormat

class ChatStrategicRightViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val csrContent = itemView.CSR_content
    private val csrDate = itemView.CSR_date


    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            csrContent.text = it.content
            when (it.user?.opinion) {
                agree -> csrContent.setTextColor(ContextCompat.getColor(context, R.color.blue))
                oppose -> csrContent.setTextColor(ContextCompat.getColor(context, R.color.red))
                neutrality -> csrContent.setTextColor(ContextCompat.getColor(context, R.color.silver))
            }
            csrDate.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.date)
        }
    }
}