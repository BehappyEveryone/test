package com.example.chatground2.view.detailForum

import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.adapter.adapterContract.CommentsAdapterContract
import com.example.chatground2.api.IpAddress
import com.squareup.picasso.Picasso
import java.io.File

interface DetailForumContract {
    interface IDetailForumPresenter{
        var idx:Int?
        var adapterModel: CommentsAdapterContract.Model?
        var adapterView: CommentsAdapterContract.View?
        fun detailForum()
        fun onCommentSendClick(isContentEmpty:Boolean,comment:String)
        fun onPathCheck(imagePath: String?)
        fun galleryResult(data: Intent?)
        fun checkCameraPermission()
        fun deleteImage()
        fun onCameraClick()
        fun closeCursor()
        fun deleteForum()
        fun modifyForum()
        fun onRecommendClick()
        fun recommendForum()
        fun deleteComment(position: Int)
        fun setServerImage(imageView: ImageView, path: String)
        fun deniedPermission()
        fun resultCancel()
    }

    interface IDetailForumView{
        fun setCameraImage(path:String?)
        fun deleteCommentImageDialog()
        fun createCommentImageDialog()
        fun progressVisible(boolean: Boolean)
        fun finishActivity()
        fun setDateText(text: String)
        fun setSubjectText(text: String)
        fun setTitleText(text: String)
        fun setContentText(text: String)
        fun setProfileImage(path: String)
        fun setNicknameText(text: String)
        fun setCommentNumText(text: String)
        fun setRecommendText(text: String)
        fun setRecommendButtonText(text: String)
        fun setRecommendButtonBackground(int:Int)
        fun setImage(imagePathList: ArrayList<String>?)
        fun setDeleteForumVisible(boolean: Boolean)
        fun setModifyForumVisible(boolean: Boolean)
        fun enterModifyForum(intent: Intent)
        fun enterModifyComment(intent: Intent)
        fun setCommentMessageText(text: String)
        fun recommendDialog(boolean: Boolean)
        fun deleteForumDialog()
        fun deleteCommentDialog(position:Int)
        fun setEnable(boolean: Boolean)
    }

    interface CallBack
    {
        fun onWriteCommentSuccess()
        fun onWriteCommentFailure()
        fun onDeleteCommentSuccess()
        fun onDeleteCommentFailure()
        fun onDetailForumSuccess(forumDto: ForumDto?)
        fun onDetailForumFailure()
        fun onDeleteForumSuccess()
        fun onDeleteForumFailure()
        fun onRecommendForumSuccess()
        fun onRecommendForumFailure()
        fun onError(t:Throwable)
    }
}