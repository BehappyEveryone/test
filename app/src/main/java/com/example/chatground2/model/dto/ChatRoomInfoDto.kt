package com.example.chatground2.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatRoomInfoDto(
    var count: Int,
    var agree:Int,
    var oppose:Int,
    var users: ArrayList<ChatUserDto>,
    var subject:String
): Parcelable