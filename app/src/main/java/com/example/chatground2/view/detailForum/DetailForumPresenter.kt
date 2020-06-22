package com.example.chatground2.view.detailForum

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.api.IpAddress
import com.example.chatground2.R
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.CommentDto
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.adapter.adapterContract.CommentsAdapterContract
import com.example.chatground2.view.dialog.CommentModifyActivity
import com.example.chatground2.view.modifyForum.ModifyForumActivity
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.DateFormat
import com.google.gson.reflect.TypeToken


class DetailForumPresenter(
    private val context: Context,
    val view: DetailForumContract.IDetailForumView
) : DetailForumContract.IDetailForumPresenter, DetailForumContract.Listener {

    private var model: Model = Model(context)
    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery:Gallery = Gallery((context))

    private val gson = Gson()
    private var commentImagePath: String? = null
    private var isRecommendExist: Boolean? = null//true면 이미 추천 false면 새로 추천

    private var imagePathArray: ArrayList<String>? = null
    private var title: String? = null
    private var content: String? = null
    private var subject: String? = null
    override var idx: Int? = null

    override var adapterModel: CommentsAdapterContract.Model? = null
    override var adapterView: CommentsAdapterContract.View? = null
        set(value) {//커스텀 접근자
            field = value
            field?.onReplyClickFunc = { position, state ->
                onReplyClick(position, state)
            }
            field?.onModifyCommentFunc = { position ->
                onModifyCommentClick(position)
            }
            field?.onDeleteCommentFunc = { position ->
                onDeleteCommentClick(position)
            }
        }

    override fun onRecommendClick() {
        isRecommendExist?.let {
            if (it) {
                view.recommendDialog(it)
            } else {
                view.recommendDialog(it)
            }
        }
    }

    override fun recommendForum() {
        view.progressVisible(true)
        view.setEnable(false)

        val hashMap = HashMap<String, Any>()
        hashMap["user"] = shared.getUser()._id
        isRecommendExist?.let { hashMap["type"] = it }

        model.recommendForum(idx.toString(), hashMap, this)
    }

    override fun onCommentSendClick() {
        view.progressVisible(true)
        view.setEnable(false)

        val hashMap = HashMap<String, RequestBody>()
        hashMap["content"] =
            RequestBody.create(MediaType.parse("text/plain"), view.getCommentMessageText())
        hashMap["user"] = RequestBody.create(MediaType.parse("text/plain"), shared.getUser()._id)
        if (!adapterModel?.getReplyCommentId().isNullOrEmpty()) {
            hashMap["replyCommentId"] = RequestBody.create(
                MediaType.parse("text/plain"),
                adapterModel?.getReplyCommentId().toString()
            )
        }

        var imagePart: MultipartBody.Part? = null

        if (!commentImagePath.isNullOrEmpty()) {
            val file = File(commentImagePath)
            val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)

            imagePart = MultipartBody.Part.createFormData("img", file.name, requestBody)
        }

        model.writeComment(idx.toString(), hashMap, imagePart, this)
    }

    override fun onCameraClick() {
        onPathCheck(commentImagePath)
    }

    override fun onPathCheck(imagePath: String?) {
        if (imagePath.isNullOrEmpty())//비었으면
        {
            view.createCommentImageDialog()
        } else {
            view.deleteCommentImageDialog()
        }
    }

    override fun deleteImage() {
        view.setCameraImage(null)
        commentImagePath = null
    }

    override fun checkCameraPermission() {
        when(permission.checkCameraPermission()){
            0 -> {//권한을 이미 허용
                view.openGallery()
            }
            1-> {//이전에 이미 권한이 거부됨
                view.toastMessage("권한을 허가해주십시오.")
                permission.setupPermissions()
            }
            3-> {//최초로 권한 요청
                permission.makeRequest()
            }
        }
    }

    override fun galleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        if (!path.isNullOrEmpty()) {
            commentImagePath = path
            view.setCameraImage(path)
        }
    }

    override fun detailForum() {
        view.progressVisible(true)
        view.setEnable(false)

        model.detailForum(idx.toString(), this)
    }

    override fun deleteForum() {
        view.progressVisible(true)
        view.setEnable(false)

        model.deleteForum(idx.toString(), this)
    }

    override fun modifyForum() {
        val intent: Intent = Intent(context, ModifyForumActivity::class.java)
        intent.putExtra("idx", idx)
        intent.putExtra("subject", subject)
        intent.putExtra("title", title)
        intent.putExtra("content", content)
        intent.putExtra("imagePathArray", imagePathArray)
        view.enterModifyForum(intent)
    }

    override fun deleteComment(position: Int) {
        view.progressVisible(true)
        view.setEnable(false)
        adapterModel?.let {
            model.deleteComment(idx.toString(), it.getItem(position)._id, this)
        }
    }

    override fun onDetailForumSuccess(forumDto: ForumDto?) {
        adapterModel?.clearItems()
        forumDto?.let {
            if (it.user._id == shared.getUser()._id) {
                view.setDeleteForumVisible(true)
                view.setModifyForumVisible(true)
            }

            isRecommendExist = it.recommend?.contains(shared.getUser()._id).apply {
                if (this == null || this == false) {
                    view.setRecommendButtonBackground(R.drawable.recommend_button_fit)
                } else {
                    view.setRecommendButtonBackground(R.drawable.recommend_button_fit2)
                }
            }

            imagePathArray = it.imageUrl
            title = it.title
            content = it.content
            subject = it.subject

            view.setSubjectText(it.subject)
            view.setTitleText(it.title)
            view.setContentText(it.content)
            view.setDateText(DateFormat.getDateInstance(DateFormat.LONG).format(it.birth))
            view.setCommentNumText("댓글 : ${it.comments?.size.toString()} 개")
            view.setRecommendText("추천 : ${it.recommend?.size.toString()} 개")
            view.setRecommendButtonText(it.recommend?.size.toString())
            it.user.profile?.let { it1 -> view.setProfileImage(IpAddress.BaseURL + it1) }
            view.setNicknameText(it.user.nickname)

            view.setImage(imagePathArray)

            it.comments?.let { it1 ->
                val commentArray = gson.fromJson<ArrayList<CommentDto>>(
                    gson.toJson(it1),
                    object : TypeToken<ArrayList<CommentDto>>() {}.type
                )

                adapterModel?.addItems(commentArray)
                adapterView?.notifyAdapter()
            }
        }
        view.progressVisible(false)
        view.setEnable(true)
    }

    override fun onDetailForumFailure() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("해당 글을 찾을 수 없습니다.")
        view.finishActivity()
    }

    override fun onDeleteForumSuccess() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("게시글 삭제 성공")
        view.finishActivity()
    }

    override fun onDeleteForumFailure() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("게시글 삭제 실패")
    }

    override fun onWriteCommentSuccess() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("댓글 작성 완료")
        view.setCommentMessageText("")

        deleteImage()
        detailForum()
        adapterModel?.setReplyCommentId(null)
    }

    override fun onWriteCommentFailure() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("댓글 작성 실패")
    }

    override fun onDeleteCommentSuccess() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("댓글 삭제 성공")
        detailForum()
    }

    override fun onDeleteCommentFailure() {
        view.progressVisible(false)
        view.setEnable(true)
        view.toastMessage("댓글 삭제 실패")
        detailForum()
    }

    override fun onRecommendForumSuccess() {
        view.progressVisible(false)
        view.setEnable(true)
        isRecommendExist?.let {
            if (it) {
                view.toastMessage("추천취소 성공")
            } else {
                view.toastMessage("추천 성공")
            }
        }
        isRecommendExist = null
        detailForum()
    }

    override fun onRecommendForumFailure() {
        view.progressVisible(false)
        view.setEnable(true)
        isRecommendExist?.let {
            if (it) {
                view.toastMessage("추천취소 실패")
            } else {
                view.toastMessage("추천 실패")
            }
        }
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        view.progressVisible(false)
        view.toastMessage("통신 에러")
    }

    private fun onReplyClick(position: Int, state: Boolean) {
        adapterModel.let {
            if (state) {
                it?.setReplyCommentId(null)
            } else {
                it?.setReplyCommentId(it.getItem(position)._id)
            }
        }
    }

    private fun onModifyCommentClick(position: Int) {
        adapterModel?.getItem(position)?.let {
            val intent: Intent = Intent(context, CommentModifyActivity::class.java)
            intent.putExtra("id", it._id)
            intent.putExtra("content", it.content)
            intent.putExtra("imagePath", it.imageUrl)

            view.enterModifyComment(intent)
        }
    }

    private fun onDeleteCommentClick(position: Int) {
        view.deleteCommentDialog(position)
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }
}