package com.example.chatground2.view.writeForum

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
import com.example.chatground2.model.Constants.SHARED_PREFERENCE
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
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

    private val sp: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val gson = Gson()

    private var c:Cursor? = null

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

    override fun closeCursor() {
        if(c != null)
        {
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
            imagePathList.add(path)
        }
        view.setImage(imagePathList)
    }

    override fun saveClick() {
        if (!view.isTitleEmpty() && !view.isContentEmpty()) {
            view.setEnable(false)
            view.progressVisible(true)
            val hashMap = HashMap<String, RequestBody>()
            hashMap["user"] = RequestBody.create(MediaType.parse("text/plain"),getUser()._id)
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

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
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
}