package com.example.chatground2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.adapter.adapterContract.ChatUserAdapterContract
import com.example.chatground2.adapter.holder.ChatUserViewHolder
import com.example.chatground2.model.dto.ChatUserDto

class ChatUserAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ChatUserAdapterContract.Model, ChatUserAdapterContract.View {
    override var onClickFunc: ((Int) -> Unit)? = null

    override fun getItem(position: Int): ChatUserDto = items[position]

    val items: ArrayList<ChatUserDto> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_drawer, parent, false)
        return ChatUserViewHolder(context, onClickFunc, view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatUserViewHolder)
        holder.onBind(items, position)
    }

    override fun getItemCount(): Int = items.size

    override fun notifyAdapter() = notifyDataSetChanged()

    override fun addItems(chatUserItems: ArrayList<ChatUserDto>) {
        this.items.addAll(chatUserItems)
    }

    override fun clearItems() {
        items.clear()
    }
}