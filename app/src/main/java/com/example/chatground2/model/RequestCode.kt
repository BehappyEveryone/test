package com.example.chatground2.model

enum class RequestCode(val code:Int) {
    OPEN_GALLERY(1),
    WRITE_FORUM(2),
    DETAIL_FORUM(3),
    MODIFY_FORUM(4),
    MODIFY_COMMENT(5),
    OPEN_VIDEO(6),
    CAMERA_REQUEST(100)
}