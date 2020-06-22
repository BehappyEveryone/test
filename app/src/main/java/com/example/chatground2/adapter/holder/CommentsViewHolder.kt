package com.example.chatground2.adapter.holder

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.dto.CommentDto
import com.example.chatground2.R
import com.example.chatground2.`class`.Shared
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_comment.view.*
import java.lang.Exception
import java.text.DateFormat

class CommentsViewHolder(
    val context: Context,
    itemView: View,
    private val onReplyClickFunc: ((Int, Boolean) -> Unit)?,
    private val onModifyCommentFunc: ((Int) -> Unit)?,
    private val onDeleteCommentFunc: ((Int) -> Unit)?
) : RecyclerView.ViewHolder(itemView) {

    private val content = itemView.CI_content
    private val date = itemView.CI_date
    private val nickname = itemView.CI_nickname
    private val replyButton = itemView.CI_replyButton
    private val image = itemView.CI_image
    private val profile = itemView.CI_profile
    private val modifyButton = itemView.CI_modifyButton
    private val deleteButton = itemView.CI_deleteButton

    private var shared:Shared = Shared(context)
    private var position: Int? = null


    fun onBind(
        items: ArrayList<CommentDto>,
        position: Int,
        replyCommentId: String?
    ) {
        this.position = position

        items[position].let {
            if (it.user._id == shared.getUser()._id) {
                modifyButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
            }
            if (replyCommentId == it._id) {
                itemView.background = ContextCompat.getDrawable(context, R.color.blue2)
                replyButton.background = ContextCompat.getDrawable(context, R.drawable.tedury3)
                modifyButton.background = ContextCompat.getDrawable(context, R.drawable.tedury3)
                deleteButton.background = ContextCompat.getDrawable(context, R.drawable.tedury3)
            } else {
                itemView.background = ContextCompat.getDrawable(context, R.color.white)
                replyButton.background = ContextCompat.getDrawable(context, R.drawable.tedury2)
                modifyButton.background = ContextCompat.getDrawable(context, R.drawable.tedury2)
                deleteButton.background = ContextCompat.getDrawable(context, R.drawable.tedury2)
            }

            content.text = it.content
            date.text = DateFormat.getDateInstance(DateFormat.LONG).format(it.birth)
            nickname.text = it.user.nickname

            if (!it.imageUrl.isNullOrEmpty()) {
                image.visibility = View.VISIBLE
                Picasso.get()
                    .load(IpAddress.BaseURL + it.imageUrl)
                    .into(image, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            Picasso.get().load(R.drawable.noimage).into(image)
                        }
                    })
            }

            if (!it.user.profile.isNullOrEmpty()) {
                Picasso.get()
                    .load(IpAddress.BaseURL + it.user.profile)
                    .into(profile, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            println("이미지 로드 에러 : $e")
                            Picasso.get().load(R.drawable.default_profile).into(profile)
                        }
                    })
            }

            replyButton.setOnClickListener { _ ->
                if (replyCommentId == it._id) {
                    onReplyClickFunc?.invoke(position, true)
                } else {
                    onReplyClickFunc?.invoke(position, false)
                }
            }

            modifyButton.setOnClickListener {
                onModifyCommentFunc?.invoke(position)
            }

            deleteButton.setOnClickListener {
                onDeleteCommentFunc?.invoke(position)
            }
        }
    }
}