package com.example.chatground2.view.writeForum

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.ToastMessage
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.model.Constant.contentText
import com.example.chatground2.model.Constant.imageUploadName
import com.example.chatground2.model.Constant.subjectText
import com.example.chatground2.model.Constant.titleText
import com.example.chatground2.model.Constant.userText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class WriteForumPresenter(val context: Context, val view: WriteForumContract.IWriteForumView
) : WriteForumContract.IWriteForumPresenter, WriteForumContract.CallBack {

    private var model: WriteForumModel = WriteForumModel(context)
    private var permission:Permission = Permission(context)
    private var shared:Shared = Shared(context)
    private var gallery:Gallery = Gallery(context)
    private var toastMessage:ToastMessage = ToastMessage(context)

    private var imagePathList: ArrayList<String> = ArrayList()

    override fun cameraClick() {
        if (imagePathList.size >= 5) {
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
                gallery.openGallery()
            }
            1-> {//이전에 이미 권한이 거부됨
                toastMessage.requestPermission()
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

    override fun saveClick(isTitleEmpty:Boolean,isContentEmpty:Boolean,subject:String,title:String,content:String) {
        if (!isTitleEmpty && !isContentEmpty) {
            view.setEnable(false)
            view.progressVisible(true)
            val hashMap = HashMap<String, RequestBody>()
            hashMap[userText] = RequestBody.create(MediaType.parse("text/plain"),shared.getUser()._id)
            hashMap[subjectText] = RequestBody.create(MediaType.parse("text/plain"),subject)
            hashMap[titleText] = RequestBody.create(MediaType.parse("text/plain"),title)
            hashMap[contentText] = RequestBody.create(MediaType.parse("text/plain"),content)

            val imagePart = arrayOfNulls<MultipartBody.Part>(imagePathList.size)

            for (i in imagePathList.indices) {
                val file: File = File(imagePathList[i])
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)

                imagePart[i] = MultipartBody.Part.createFormData(imageUploadName,file.name,requestBody)
            }

            model.writeForum(hashMap, imagePart, this)
        } else {
            if (isTitleEmpty) {
                toastMessage.titleNull()
            }
            if (isContentEmpty) {
                toastMessage.contentNull()
            }
        }
    }

    override fun deniedPermission() {
        toastMessage.deniedPermission()
    }

    override fun resultCancel() {
        toastMessage.resultCancel()
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun onSuccess() {
        view.progressVisible(false)
        toastMessage.retrofitSuccess()
        view.finishActivity()
    }

    override fun onFailure() {
        view.progressVisible(false)
        toastMessage.retrofitFailure()
        view.setEnable(true)
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
        view.progressVisible(false)
        toastMessage.retrofitError()
        view.setEnable(true)
    }
}