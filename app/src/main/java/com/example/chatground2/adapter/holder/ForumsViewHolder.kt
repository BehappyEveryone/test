package com.example.chatground2.adapter.holder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.R
import com.example.chatground2.model.dto.ForumDto
import kotlinx.android.synthetic.main.item_forums.view.*
import java.text.DateFormat

class ForumsViewHolder(
    val context: Context, private val listenerFunc: ((Int) -> Unit)?,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val subject: TextView = itemView.FI_subject
    private val title: TextView = itemView.FI_title
    private val commentNum: TextView = itemView.FI_commentNum
    private val recommendText: TextView = itemView.FI_recommendtext
    private val nickname: TextView = itemView.FI_nickname
    private val dateText: TextView = itemView.FI_date
    private val image: ImageView = itemView.FI_image

    fun onBind(items: ArrayList<ForumDto>, position: Int) {
        items[position].let {
            subject.text = context.getString(R.string.holder_subject,it.subject)
            title.text = it.title
            commentNum.text = context.getString(R.string.holder_num,it.comments?.size)
            recommendText.text = context.getString(R.string.holder_num,it.recommend?.size)
            nickname.text = it.user.nickname
            dateText.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.birth)
            if (it.imageUrl.isNullOrEmpty()) {
                image.visibility = View.INVISIBLE
            } else {
                image.visibility = View.VISIBLE
            }
        }
        itemView.setOnClickListener {
            listenerFunc?.invoke(position)
        }
    }
}