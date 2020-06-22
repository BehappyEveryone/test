package com.example.chatground2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.`class`.Shared
import com.example.chatground2.adapter.adapterContract.ChatAdapterContract
import com.example.chatground2.adapter.holder.*
import com.example.chatground2.model.dto.ChatDto

class ChatAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ChatAdapterContract.Model, ChatAdapterContract.View {

    override var onVideoClickFunc:((Int) -> Unit)? = null

    override fun getItem(position: Int): ChatDto = items[position]

    override fun getItemSize(): Int = itemCount

    val items: ArrayList<ChatDto> = ArrayList()

    private var shared: Shared = Shared(context)

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            "system" -> 0
            "text" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    1//right
                } else {
                    2//left
                }
            }
            "strategic" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    3//right
                } else {
                    4//left
                }
            }
            "image" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    5//right
                } else {
                    6//left
                }
            }
            "video" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    7//right
                } else {
                    8//left
                }
            }
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_system, parent, false)
                ChatSystemViewHolder(context, view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_right, parent, false)
                ChatTextRightViewHolder(context, view)
            }
            2 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_left, parent, false)
                ChatTextLeftViewHolder(context, view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_strategic_right, parent, false)
                ChatStrategicRightViewHolder(context, view)
            }
            4 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_strategic_left, parent, false)
                ChatStrategicLeftViewHolder(context, view)
            }
            5 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_right, parent, false)
                ChatImageRightViewHolder(context, view)
            }
            6 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_image_left, parent, false)
                ChatImageLeftViewHolder(context, view)
            }
            7 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_video_right, parent, false)
                ChatVideoRightViewHolder(context, view,onVideoClickFunc)
            }
            8 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_video_left, parent, false)
                ChatVideoLeftViewHolder(context, view,onVideoClickFunc)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (items[position].type) {
            "system" -> {
                (holder as ChatSystemViewHolder)
                holder.onBind(items, position)
            }
            "text" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    (holder as ChatTextRightViewHolder)
                    holder.onBind(items, position)
                } else {
                    (holder as ChatTextLeftViewHolder)
                    holder.onBind(items, position)
                }
            }
            "strategic" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    (holder as ChatStrategicRightViewHolder)
                    holder.onBind(items, position)
                } else {
                    (holder as ChatStrategicLeftViewHolder)
                    holder.onBind(items, position)
                }
            }
            "image" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    (holder as ChatImageRightViewHolder)
                    holder.onBind(items, position)
                } else {
                    (holder as ChatImageLeftViewHolder)
                    holder.onBind(items, position)
                }
            }
            "video" -> {
                if (items[position].user?._id == shared.getUser()._id) {
                    (holder as ChatVideoRightViewHolder)
                    holder.onBind(items, position)
                } else {
                    (holder as ChatVideoLeftViewHolder)
                    holder.onBind(items, position)
                }
            }
            else -> -1
        }
    }

    override fun getItemCount(): Int = items.size

    override fun notifyAdapter() = notifyDataSetChanged()

    override fun addItems(chatItems: ArrayList<ChatDto>) {
        this.items.addAll(chatItems)
    }

    override fun addItem(chatItem: ChatDto) {
        this.items.add(chatItem)
        println("채팅아이템 : ${this.items}")
    }

    override fun clearItems() {
        items.clear()
    }
}