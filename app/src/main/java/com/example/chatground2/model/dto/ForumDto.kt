package com.example.chatground2.model.dto

import java.util.*
import kotlin.collections.ArrayList

data class ForumDto(
    var _id:String,
    var idx: Int,
    var user:UserDto,
    var subject:String,
    var title:String,
    var content:String,
    var imageUrl:ArrayList<String>?,
    var comments:ArrayList<Any>?,
    var recommend:ArrayList<String>?,
    var birth:Date
)