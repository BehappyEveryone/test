package com.example.chatground2.adapter.adapterContract

import com.example.chatground2.model.dto.ForumDto

interface ForumsAdapterContract {
    interface View {
        var onClickFunc : ((Int) -> Unit)?//1급객체
        fun notifyAdapter()
    }

    interface Model {
        fun addItems(forumItems:ArrayList<ForumDto>)
        fun clearItems()
        fun getItem(position:Int): ForumDto
    }
}