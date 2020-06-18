package com.example.chatground2.view.profile

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
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.Constants
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfilePresenter(private val context: Context, val view: ProfileContract.IProfileView) :
    ProfileContract.IProfilePresenter, ProfileContract.Listener {

    private var model: Model = Model(context)

    private val sp: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    private val spEdit: SharedPreferences.Editor = sp.edit()
    private val gson = Gson()
    private var image: String? = null
    private var c: Cursor? = null

    override fun closeCursor() {
        if (c != null) {
            c?.close()
        }
    }

    override fun profileInit() {
        view.setEmail(getUser().email)
        view.setNickname(getUser().nickname)
        getUser().introduce?.let { view.setIntroduce(it) }
        getUser().profile?.let {
            image = it
            view.setProfileImage(IpAddress.BaseURL + it)
        }
    }

    override fun logout() {
        view.finishActivity()
        view.enterLoginActivity()
        spEdit.clear()
        spEdit.commit()
    }

    override fun logoutClick() {
        view.logoutDialog()
    }

    override fun profileImageClick() {
        view.imageDialog()
    }

    override fun saveProfile() {
        view.setEnable(false)
        view.progressVisible(true)
        val hashMap = HashMap<String, RequestBody>()
        hashMap["introduce"] =
            RequestBody.create(MediaType.parse("text/plain"), view.getIntroduce())

        var imagePart: MultipartBody.Part? = null

        image?.let {
            if (image?.substring(0, 13) == "profileImages") {
                hashMap["profile"] = RequestBody.create(MediaType.parse("text/plain"), it)
            } else {
                val file: File = File(image)
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
                imagePart = MultipartBody.Part.createFormData("img", file.name, requestBody)
            }
        }

        model.modifyProfile(getUser().email,hashMap, imagePart, this)
    }

    override fun callUser() {
        view.setEnable(false)
        view.progressVisible(true)

        model.callUser(getUser().email,this)
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

    override fun defaultImage() {
        view.setProfileImage(null)
        image = null
    }

    override fun galleryResult(data: Intent?) {
        val currentImageUrl: Uri? = data?.data
        val path = getPath(currentImageUrl!!)
        image = path
        image?.let { view.setProfileImage("file://$it") }
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

    override fun onCallUserSuccess(user: UserDto) {
        spEdit.remove("User")//기존 유저 데이터 삭제
        val userJson = gson.toJson(user)
        spEdit.putString("User", userJson)
        spEdit.commit()
        user.introduce?.let { view.setIntroduce(it) }
        if (user.profile != null) {
            view.setProfileImage(IpAddress.BaseURL + user.profile)
        } else {
            view.setProfileImage(user.profile)
        }
        view.setEnable(true)
        view.progressVisible(false)
    }

    override fun onCallUserFailure() {
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("불러오기 실패")
    }

    override fun onSaveSuccess() {
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("저장 완료")

        callUser()
    }

    override fun onSaveFailure() {
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("저장 실패")
    }

    override fun onError(t:Throwable) {
        t.printStackTrace()
        view.setEnable(true)
        view.progressVisible(false)
        view.toastMessage("통신 실패")
    }

    private fun getUser(): UserDto {
        val json = sp.getString("User", "")
        return gson.fromJson(json, UserDto::class.java)
    }
}