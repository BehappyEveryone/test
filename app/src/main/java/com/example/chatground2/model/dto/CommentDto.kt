package com.example.chatground2.model.dto

import java.util.*
import kotlin.collections.ArrayList

data class CommentDto(
    var _id:String,
    var forumIdx:Int,
    var replyCommentId:String?,
    var user:UserDto,
    var content:String,
    var imageUrl:String?,
    var replies:ArrayList<Any>?,
    var birth:Date
)