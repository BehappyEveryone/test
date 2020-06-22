package com.example.chatground2.view.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.Permission
import com.example.chatground2.model.dao.Model

class CommentModifyPresenter(
    private val context: Context,
    val view: CommentModifyContract.ICommentModifyView
) : CommentModifyContract.ICommentModifyPresenter, CommentModifyContract.Listener{

    private var model: Model = Model(context)
    private var permission: Permission = Permission(context)
    private var gallery:Gallery = Gallery(context)

    private var commentImagePath: String? = null
    private var id:String? = null

    override fun getIntent(intent: Intent) {
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

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun modifyComment() {

    }

    override fun onModifyCommentSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onModifyCommentFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}