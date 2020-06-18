package com.example.chatground2.model.dto

import java.util.*

data class ChatDto(
    var type: String,
    var content: String,
    var image: String?,
    var date: Date,
    var user: ChatUserDto?
)