package com.example.chatground2.model.dto

import java.util.*
import kotlin.collections.ArrayList

data class UserDto(
    var _id: String,
    var email: String,
    var password: String?,
    var nickname: String,
    var birth: Date,
    var profile: String?,
    var introduce: String?,
    var forums: ArrayList<Any>?,
    var comments: ArrayList<Any>?
)