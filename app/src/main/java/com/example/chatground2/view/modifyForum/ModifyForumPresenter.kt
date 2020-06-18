package com.example.chatground2.view.modifyForum

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ModifyForumPresenter(
    private val context: Context, val view: ModifyForumContract.IModifyForumView
) : ModifyForumContract.IModifyForumPresenter, ModifyForumContract.Listener {

    private var model: Model = Model(context)

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private var c: Cursor? = null

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

    override fun closeCursor() {
        if (c != null) {
            c?.close()
        }
    }

    override fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        )//
        {
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

    override fun galleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = getPath(currentImageUrl!!)
        if (!path.isNullOrEmpty()) {
            imagePathArray.add(path)
        }
        view.setImage(imagePathArray)
    }

    private fun getPath(uri: Uri): String? {
        val pro: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        c = context.contentResolver.query(uri, pro, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        return index?.let { c?.getString(it) }
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

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("통신 실패")
    }

    override fun saveClick() {
        if (!view.isTitleEmpty() && !view.isContentEmpty()) {
            view.setEnable(false)
            view.progressVisible(true)

            val hashMap = HashMap<String, RequestBody>()
            hashMap["user"] = RequestBody.create(MediaType.parse("text/plain"), getUser()._id)
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

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}