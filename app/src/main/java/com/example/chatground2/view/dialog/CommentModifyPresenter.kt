package com.example.chatground2.view.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.`class`.Permission
import com.example.chatground2.`class`.Shared
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class CommentModifyPresenter(
    val context: Context,
    val view: CommentModifyContract.ICommentModifyView
) : CommentModifyContract.ICommentModifyPresenter, CommentModifyContract.Listener {

    private var model: CommentModifyModel = CommentModifyModel(context)
    private var permission: Permission = Permission(context)
    private var gallery: Gallery = Gallery(context)
    private var shared: Shared = Shared(context)
    private var toastMessage: ToastMessage = ToastMessage(context)

    private var commentImagePath: String? = null
    private var idx: String? = null
    private var id: String? = null

    override fun getIntent(intent: Intent) {
        idx = intent.getStringExtra("idx")
        id = intent.getStringExtra("id")
        view.setContentText(intent.getStringExtra("content"))
        view.setCameraImage(intent.getStringExtra("imagePath"))
        commentImagePath = intent.getStringExtra("imagePath")
    }

    override fun cameraClick() {
        onPathCheck(commentImagePath)
    }

    override fun cancel() {
        view.finishActivity()
    }

    override fun galleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        if (!path.isNullOrEmpty()) {
            commentImagePath = path
            view.setCameraImage(path)
        }
    }

    override fun onPathCheck(imagePath: String?) {
        if (imagePath.isNullOrEmpty())//비었으면
        {
            checkCameraPermission()
        } else {
            view.deleteCommentImageDialog()
        }
    }

    override fun deleteImage() {
        view.setCameraImage(null)
        commentImagePath = null
    }

    override fun checkCameraPermission() {
        when (permission.checkCameraPermission()) {
            0 -> {//권한을 이미 허용
                gallery.openGallery()
            }
            1 -> {//이전에 이미 권한이 거부됨
                toastMessage.requestPermission()
                permission.setupPermissions()
            }
            3 -> {//최초로 권한 요청
                permission.makeRequest()
            }
        }
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun modifyComment(isContentEmpty: Boolean, content: String) {
        if (isContentEmpty) {
            toastMessage.contentNull()
        } else {
            view.setEnable(false)
            view.progressVisible(true)

            val hashMap = HashMap<String, RequestBody>()
            hashMap["user"] =
                RequestBody.create(MediaType.parse("text/plain"), shared.getUser()._id)
            hashMap["content"] = RequestBody.create(MediaType.parse("text/plain"), content)

            var imagePart: MultipartBody.Part? = null

            if (!commentImagePath.isNullOrEmpty()) {
                imagePart = if (commentImagePath?.substring(0, 11) == "forumImages") {
                    MultipartBody.Part.createFormData("imageUrl", commentImagePath)
                } else {
                    val file: File = File(commentImagePath)
                    val requestBody: RequestBody =
                        RequestBody.create(MediaType.parse("image/*"), file)
                    MultipartBody.Part.createFormData("img", file.name, requestBody)
                }
            }

            model.modifyComment(idx.toString(),id.toString(), hashMap, imagePart, this)
        }
    }

    override fun deniedPermission() {
        toastMessage.deniedPermission()
    }

    override fun resultCancel() {
        toastMessage.resultCancel()
    }

    override fun onModifyCommentSuccess() {
    }

    override fun onModifyCommentFailure() {
    }

    override fun onError(t: Throwable) {

    }
}