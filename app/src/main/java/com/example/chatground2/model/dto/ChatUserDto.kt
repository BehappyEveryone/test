package com.example.chatground2.model.dto

data class ChatUserDto(
    var _id: String,
    var nickname: String,
    var profile: String?,
    var introduce: String?,
    var opinion:String?
)