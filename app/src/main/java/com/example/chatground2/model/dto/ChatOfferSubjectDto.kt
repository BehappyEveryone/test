package com.example.chatground2.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ChatOfferSubjectDto(
    var subject: String,
    var time: Int
):Parcelable