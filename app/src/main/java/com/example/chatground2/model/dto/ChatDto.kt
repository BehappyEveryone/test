package com.example.chatground2.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ChatDto(
    var type: String,
    var content: String,
    var date: Date,
    var binaryData:String?,
    var user: ChatUserDto?
):Parcelable