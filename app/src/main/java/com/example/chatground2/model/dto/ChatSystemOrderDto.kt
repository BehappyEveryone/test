package com.example.chatground2.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChatSystemOrderDto(
    var time: Int,
    var order: String,
    var speaking: String
):Parcelable