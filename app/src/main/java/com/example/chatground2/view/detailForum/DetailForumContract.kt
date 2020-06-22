package com.example.chatground2.view.detailForum

import android.content.Intent
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.adapter.adapterContract.CommentsAdapterContract

interface DetailForumContract {
    interface IDetailForumPresenter{
        var idx:Int?
        var adapterModel: CommentsAdapterContract.Model?
        var adapterView: CommentsAdapterContract.View?
        fun detailForum()
        fun onCommentSendClick()
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
    }

    interface IDetailForumView{
        fun setCameraImage(path:String?)
        fun deleteCommentImageDialog()
        fun createCommentImageDialog()
        fun progressVisible(boolean: Boolean)
        fun toastMessage(text:String)
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
        fun setImage0(path: String)
        fun setImage1(path: String)
        fun setImage2(path: String)
        fun setImage3(path: String)
        fun setImage4(path: String)
        fun setDeleteForumVisible(boolean: Boolean)
        fun setModifyForumVisible(boolean: Boolean)
        fun enterModifyForum(intent: Intent)
        fun enterModifyComment(intent: Intent)
        fun getCommentMessageText():String
        fun setCommentMessageText(text: String)
        fun recommendDialog(boolean: Boolean)
        fun deleteForumDialog()
        fun deleteCommentDialog(position:Int)
        fun setEnable(boolean: Boolean)
        fun openGallery()
    }

    interface Listener
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