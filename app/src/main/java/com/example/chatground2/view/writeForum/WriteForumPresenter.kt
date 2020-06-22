package com.example.chatground2.view.writeForum

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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WriteForumPresenter(
    private val context: Context, val view: WriteForumContract.IWriteForumView
) : WriteForumContract.IWriteForumPresenter, WriteForumContract.Listener {

    private var model: Model = Model(context)
    private var permission:Permission = Permission(context)
    private var shared:Shared = Shared(context)
    private var gallery:Gallery = Gallery(context)

    private var imagePathList: ArrayList<String> = ArrayList()

    override fun cameraClick() {
        if (imagePathList.size >= 5) {
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
            1 -> imagePathList.removeAt(0)
            2 -> imagePathList.removeAt(1)
            3 -> imagePathList.removeAt(2)
            4 -> imagePathList.removeAt(3)
            5 -> imagePathList.removeAt(4)
        }
        view.setImage(imagePathList)
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
            imagePathList.add(path)
        }
        view.setImage(imagePathList)
    }

    override fun saveClick() {
        if (!view.isTitleEmpty() && !view.isContentEmpty()) {
            view.setEnable(false)
            view.progressVisible(true)
            val hashMap = HashMap<String, RequestBody>()
            hashMap["user"] = RequestBody.create(MediaType.parse("text/plain"),shared.getUser()._id)
            hashMap["subject"] = RequestBody.create(MediaType.parse("text/plain"),view.getSelectSubject())
            hashMap["title"] = RequestBody.create(MediaType.parse("text/plain"),view.getTitleText())
            hashMap["content"] = RequestBody.create(MediaType.parse("text/plain"),view.getContentText())

            val imagePart = arrayOfNulls<MultipartBody.Part>(imagePathList.size)

            for (i in imagePathList.indices) {
                val file: File = File(imagePathList[i])
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)

                imagePart[i] = MultipartBody.Part.createFormData("img",file.name,requestBody)
            }

            model.writeForum(hashMap, imagePart, this)
        } else {
            if (view.isTitleEmpty()) {
                view.toastMessage("제목을 입력해주세요.")
            }
            if (view.isContentEmpty()) {
                view.toastMessage("내용을 입력해주세요.")
            }
        }
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun onSuccess() {
        view.progressVisible(false)
        view.toastMessage("생성 완료")
        view.finishActivity()
    }

    override fun onFailure() {
        view.progressVisible(false)
        view.toastMessage("잠시 후 다시 시도해주세요")
        view.setEnable(true)
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        view.progressVisible(false)
        view.toastMessage("통신 실패")
        view.setEnable(true)
    }
}