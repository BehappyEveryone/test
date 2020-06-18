package com.example.chatground2.view.dialog

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class CommentModifyPresenter(
    private val context: Context,
    val view: CommentModifyContract.ICommentModifyView
) : CommentModifyContract.ICommentModifyPresenter, CommentModifyContract.Listener{
    private var c: Cursor? = null
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

    override fun closeCursor() {
        if (c != null) {
            c?.close()
        }
    }

    override fun galleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = getPath(currentImageUrl!!)
        if (!path.isNullOrEmpty()) {
            commentImagePath = path
            view.setCameraImage(path)
        }
    }

    private fun getPath(uri: Uri): String? {
        val pro: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        c = context.contentResolver.query(uri, pro, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        return index?.let { c?.getString(it) }
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
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {//이전에 이미 권한이 거부되었을 때 설명
                view.toastMessage("권한을 허가해주십시오.")
                setupPermissions()
            } else {//최초로 권한 요청
                makeRequest()
            }
        } else {
            view.openGallery()
        }
    }

    private fun setupPermissions() {
        //스토리지 읽기 퍼미션을 permission 변수에 담는다
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            100
        )
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