package com.example.chatground2.adapter.adapterContract

import com.example.chatground2.model.dto.ChatUserDto

interface ChatUserAdapterContract {
    interface View {
        var onClickFunc : ((Int) -> Unit)?//1급객체
        fun notifyAdapter()
    }

    interface Model {
        fun addItems(chatUserItems:ArrayList<ChatUserDto>)
        fun clearItems()
        fun getItem(position:Int): ChatUserDto
    }
}