package com.example.chatground2.adapter.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.model.dto.ChatDto
import kotlinx.android.synthetic.main.item_chat_system.view.*

class ChatSystemViewHolder(
    val context: Context,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val content = itemView.CS_content

    fun onBind(items: ArrayList<ChatDto>, position: Int) {
        items[position].let {
            content.text = it.content
        }
    }
}