package com.example.chatground2.view.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chatground2.`class`.Gallery
import com.example.chatground2.`class`.Shared
import com.example.chatground2.`class`.Permission
import com.example.chatground2.api.IpAddress
import com.example.chatground2.model.dao.Model
import com.example.chatground2.model.dto.UserDto
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfilePresenter(private val context: Context, val view: ProfileContract.IProfileView) :
    ProfileContract.IProfilePresenter, ProfileContract.Listener {

    private var model: Model = Model(context)
    private var permission: Permission = Permission(context)
    private var shared: Shared = Shared(context)
    private var gallery:Gallery = Gallery(context)
    private var image: String? = null

    override fun profileInit() {
        shared.getUser().run {
            view.setEmail(this.email)
            view.setNickname(this.nickname)
            this.introduce?.let { view.setIntroduce(it) }
            this.profile?.let {
                image = it
                view.setProfileImage(IpAddress.BaseURL + it)
            }
        }
    }

    override fun logout() {
        view.finishActivity()
        view.enterLoginActivity()
        shared.editorClear()
        shared.editorCommit()
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

        model.modifyProfile(shared.getUser().email,hashMap, imagePart, this)
    }

    override fun callUser() {
        view.setEnable(false)
        view.progressVisible(true)

        model.callUser(shared.getUser().email,this)
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

    override fun defaultImage() {
        view.setProfileImage(null)
        image = null
    }

    override fun galleryResult(data: Intent?) {
        println("이미지")
        val currentImageUrl: Uri? = data?.data
        val path = gallery.getPath(currentImageUrl!!)
        println("이미지2 : $image")
        image = path
        image?.let { view.setProfileImage("file://$it") }
    }

    override fun closeCursor() {
        gallery.closeCursor()
    }

    override fun onCallUserSuccess(user: UserDto) {
        shared.editorRemove("User")
        val userJson = shared?.gsonToJson(user)
        shared.setSharedPreference("User",userJson)
        shared.editorCommit()

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
}