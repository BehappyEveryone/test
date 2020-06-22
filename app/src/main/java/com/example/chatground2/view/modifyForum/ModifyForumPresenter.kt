package com.example.chatground2.view.modifyForum

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.model.dao.Model
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ModifyForumPresenter(
    private val context: Context, val view: ModifyForumContract.IModifyForumView
) : ModifyForumContract.IModifyForumPresenter, ModifyForumContract.Listener {

    private var model: Model = Model(context)
    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery:Gallery = Gallery(context)

    private var idx: Int? = null
    private var imagePathArray: ArrayList<String> = ArrayList()

    override fun getIntent(intent: Intent) {
        intent.getStringExtra("subject")
        intent.getStringExtra("title")
        intent.getStringExtra("content")
        imagePathArray.addAll(intent.getSerializableExtra("imagePathArray") as ArrayList<String>)
        idx = intent.getIntExtra("idx", -1)

        view.setDefault(intent.getStringExtra("subject"),intent.getStringExtra("title"),intent.getStringExtra("content"))
        view.setImage(imagePathArray)
        //셋이미지
    }

    override fun saveClick() {
        if (!view.isTitleEmpty() && !view.isContentEmpty()) {
            view.setEnable(false)
            view.progressVisible(true)

            val hashMap = HashMap<String, RequestBody>()
            hashMap["user"] = RequestBody.create(MediaType.parse("text/plain"), shared.getUser()._id)
            hashMap["subject"] =
                RequestBody.create(MediaType.parse("text/plain"), view.getSelectSubject())
            hashMap["title"] =
                RequestBody.create(MediaType.parse("text/plain"), view.getTitleText())
            hashMap["content"] =
                RequestBody.create(MediaType.parse("text/plain"), view.getContentText())

            val imagePart = arrayOfNulls<MultipartBody.Part?>(imagePathArray.size)

            for (i in imagePathArray.indices) {
                if (imagePathArray[i].substring(0, 11) == "forumImages") {
                    imagePart[i] = MultipartBody.Part.createFormData("imageUrl",imagePathArray[i])
                } else {
                    val file: File = File(imagePathArray[i])
                    val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                    imagePart[i] = MultipartBody.Part.createFormData("img", file.name, requestBody)
                }
            }

            model.modifyForum(idx.toString(),hashMap, imagePart, this)
        } else {
            if (view.isTitleEmpty()) {
                view.toastMessage("제목을 입력해주세요.")
            }
            if (view.isContentEmpty()) {
                view.toastMessage("내용을 입력해주세요.")
            }
        }
    }

    override fun cameraClick() {
        if (imagePathArray.size >= 5) {
            view.toastMessage("사진은 5개까지 가능합니다.")
        } else {
            view.createDialog()
        }
    }

    override fun showImageClick(imageNum: Int) {
        view.createShowImageDialog(imageNum)
    }

    override fun deleteImage(imageNum: Int) {
        when (imageNum) {
            1 -> imagePathArray.removeAt(0)
            2 -> imagePathArray.removeAt(1)
            3 -> imagePathArray.removeAt(2)
            4 -> imagePathArray.removeAt(3)
            5 -> imagePathArray.removeAt(4)
        }
        view.setImage(imagePathArray)
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
            imagePathArray.add(path)
        }
        view.setImage(imagePathArray)
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun onModifySuccess() {
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("수정 완료")
        view.finishActivity()
    }

    override fun onModifyFailure() {
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("수정 실패")
    }

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("통신 실패")
    }
}