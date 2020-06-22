package com.example.chatground2.adapter.adapterContract

import com.example.chatground2.model.dto.ChatDto

interface ChatAdapterContract {
    interface View {
        var onVideoClickFunc:((Int) -> Unit)?
        fun notifyAdapter()
    }

    interface Model {
        fun addItem(chatItem: ChatDto)
        fun getItemSize():Int
        fun addItems(chatItems:ArrayList<ChatDto>)
        fun clearItems()
        fun getItem(position:Int): ChatDto
    }
}