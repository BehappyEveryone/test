package com.example.chatground2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.model.dto.CommentDto
import com.example.chatground2.R
import com.example.chatground2.adapter.adapterContract.CommentsAdapterContract
import com.example.chatground2.adapter.holder.CommentsViewHolder
import com.example.chatground2.adapter.holder.ReplyViewHolder

class CommentsAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    CommentsAdapterContract.Model,
    CommentsAdapterContract.View {

    override fun getItem(position: Int): CommentDto = items[position]

    override var onReplyClickFunc: ((Int, Boolean) -> Unit)? = null
    override var onDeleteCommentFunc: ((Int) -> Unit)? = null
    override var onModifyCommentFunc: ((Int) -> Unit)? = null

    override fun getItemSize(): Int = itemCount

    val items: ArrayList<CommentDto> = ArrayList()

    private var replyCommentId: String? = null

    override fun getReplyCommentId(): String? = replyCommentId

    override fun setReplyCommentId(id: String?) {
        replyCommentId = id
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position].replyCommentId.isNullOrEmpty() -> 0
            !items[position].replyCommentId.isNullOrEmpty() -> 1

            else -> 100
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_comment, parent, false)
                CommentsViewHolder(context, view, onReplyClickFunc,onModifyCommentFunc,onDeleteCommentFunc)
            }
            1 -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_reply, parent, false)
                ReplyViewHolder(context, view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            items[position].replyCommentId.isNullOrEmpty() -> {
                (holder as CommentsViewHolder)
                holder.onBind(items, position, replyCommentId)
            }
            !items[position].replyCommentId.isNullOrEmpty() -> {
                (holder as ReplyViewHolder)
                holder.onBind(items, position)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    override fun notifyAdapter() = notifyDataSetChanged()

    override fun addItems(commentItems: ArrayList<CommentDto>) {
        this.items.addAll(commentItems)
    }

    override fun clearItems() {
        items.clear()
    }
}