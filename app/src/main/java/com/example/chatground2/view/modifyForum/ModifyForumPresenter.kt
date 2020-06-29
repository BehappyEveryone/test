package com.example.chatground2.view.modifyForum

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.model.Constant.contentText
import com.example.chatground2.model.Constant.idxText
import com.example.chatground2.model.Constant.imagePathArrayText
import com.example.chatground2.model.Constant.imageUploadName
import com.example.chatground2.model.Constant.imageUrlText
import com.example.chatground2.model.Constant.subjectText
import com.example.chatground2.model.Constant.titleText
import com.example.chatground2.model.Constant.userText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ModifyForumPresenter(
    val context: Context, val view: ModifyForumContract.IModifyForumView
) : ModifyForumContract.IModifyForumPresenter, ModifyForumContract.CallBack {

    private var model: ModifyForumModel = ModifyForumModel(context)
    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery: Gallery = Gallery(context)
    private var toastMessage: ToastMessage = ToastMessage(context)

    private var idx: Int? = null
    private var imagePathArray: ArrayList<String> = ArrayList()

    override fun getIntent(intent: Intent) {
        imagePathArray.addAll(intent.getSerializableExtra(imagePathArrayText) as ArrayList<String>)
        idx = intent.getIntExtra(idxText, -1)

        view.setDefault(
            intent.getStringExtra(subjectText),
            intent.getStringExtra(titleText),
            intent.getStringExtra(contentText)
        )
        view.setImage(imagePathArray)
        //셋이미지
    }

    override fun saveClick(
        isTitleEmpty: Boolean,
        isContentEmpty: Boolean,
        subject: String,
        title: String,
        content: String
    ) {
        if (!isTitleEmpty && !isContentEmpty) {
            view.setEnable(false)
            view.progressVisible(true)

            val hashMap = HashMap<String, RequestBody>()
            hashMap[userText] =
                RequestBody.create(MediaType.parse("text/plain"), shared.getUser()._id)
            hashMap[subject] =
                RequestBody.create(MediaType.parse("text/plain"), subject)
            hashMap[titleText] =
                RequestBody.create(MediaType.parse("text/plain"), title)
            hashMap[contentText] =
                RequestBody.create(MediaType.parse("text/plain"), content)

            val imagePart = arrayOfNulls<MultipartBody.Part?>(imagePathArray.size)

            for (i in imagePathArray.indices) {
                if (gallery.isServerExistFile(imagePathArray[i])) {
                    imagePart[i] =
                        MultipartBody.Part.createFormData(imageUrlText, imagePathArray[i])
                } else {
                    val file: File = File(imagePathArray[i])
                    val requestBody: RequestBody =
                        RequestBody.create(MediaType.parse("image/*"), file)
                    imagePart[i] =
                        MultipartBody.Part.createFormData(imageUploadName, file.name, requestBody)
                }
            }

            model.modifyForum(idx.toString(), hashMap, imagePart, this)
        } else {
            if (isTitleEmpty) {
                toastMessage.titleNull()
            }
            if (isContentEmpty) {
                toastMessage.contentNull()
            }
        }
    }

    override fun cameraClick() {
        if (imagePathArray.size >= 5) {
            toastMessage.imageOver()
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

    override fun deniedPermission() {
        toastMessage.deniedPermission()
    }

    override fun resultCancel() {
        toastMessage.resultCancel()
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
        toastMessage.retrofitSuccess()
        view.finishActivity()
    }

    override fun onModifyFailure() {
        view.setEnable(true)
        view.progressVisible(false)
        toastMessage.retrofitFailure()
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        view.progressVisible(false)
        toastMessage.retrofitError()
    }
}