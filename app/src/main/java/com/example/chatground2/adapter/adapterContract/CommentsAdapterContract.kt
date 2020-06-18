package com.example.chatground2.adapter.adapterContract

import com.example.chatground2.model.dto.CommentDto

interface CommentsAdapterContract {
    interface View {
        var onModifyCommentFunc: ((Int) -> Unit)?
        var onDeleteCommentFunc: ((Int) -> Unit)?
        var onReplyClickFunc : ((Int,Boolean) -> Unit)?//1급객체
        fun notifyAdapter()
    }

    interface Model {
        fun getItemSize():Int
        fun setReplyCommentId(id:String?)
        fun getReplyCommentId():String?
        fun addItems(commentItems:ArrayList<CommentDto>)
        fun clearItems()
        fun getItem(position:Int): CommentDto
    }
}