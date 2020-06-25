package com.example.chatground2.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatUserDto(
    var _id: String,
    var nickname: String,
    var profile: String?,
    var introduce: String?,
    var opinion:String?
):Parcelable