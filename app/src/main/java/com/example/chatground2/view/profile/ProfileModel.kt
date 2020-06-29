package com.example.chatground2.view.profile

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.model.dto.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    fun callUser(
        email: String,
        callBack: ProfileContract.CallBack
    ) {
        serviceGenerator.instance.callUser(email)
            .enqueue(object : Callback<UserDto> {
                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<UserDto>,
                    response: Response<UserDto>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { callBack.onCallUserSuccess(it) }
                    } else {
                        callBack.onCallUserFailure()
                    }
                }
            })
    }

    //프로필 수정
    fun modifyProfile(
        email:String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        callBack: ProfileContract.CallBack
    ) {
        serviceGenerator.instance.modifyProfile(email,hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callBack.onSaveSuccess()
                } else {
                    callBack.onSaveFailure()
                }
            }
        })
    }
}