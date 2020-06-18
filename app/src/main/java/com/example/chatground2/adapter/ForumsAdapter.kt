package com.example.chatground2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.R
import com.example.chatground2.adapter.adapterContract.ForumsAdapterContract
import com.example.chatground2.adapter.holder.ForumsViewHolder
import kotlin.collections.ArrayList

class ForumsAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ForumsAdapterContract.Model,
    ForumsAdapterContract.View {

    override fun getItem(position: Int): ForumDto = items[position]

    override var onClickFunc: ((Int) -> Unit)? = null

    var items: ArrayList<ForumDto> = ArrayList()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ForumsViewHolder)
        holder.onBind(items, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_forums, parent, false)
        return ForumsViewHolder(context, onClickFunc, view)
    }

    override fun getItemCount(): Int = items.size

    override fun notifyAdapter() = notifyDataSetChanged()

    override fun addItems(forumItems: ArrayList<ForumDto>) {
        this.items.addAll(forumItems)
    }

    override fun clearItems() {
        items.clear()
    }
}