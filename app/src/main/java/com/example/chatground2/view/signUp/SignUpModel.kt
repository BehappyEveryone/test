package com.example.chatground2.view.signUp

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpModel(context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //이메일 중복체크
    fun emailOverlap(
        hashMap: HashMap<String, Any>,
        callBack: SignUpContract.CallBack
    ) {
        serviceGenerator.instance.emailOverlap(hashMap).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callBack.onEmailOverlapSuccess()
                } else {
                    callBack.onEmailOverlapFailure()
                }
            }
        })
    }

    //닉네임 중복 체크
    fun nicknameOverlap(
        hashMap: HashMap<String, Any>,
        callBack: SignUpContract.CallBack
    ) {
        serviceGenerator.instance.nicknameOverlap(hashMap)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        callBack.onNicknameOverlapSuccess()
                    } else {
                        callBack.onNicknameOverlapFailure()
                    }
                }
            })
    }

    //회원가입
    fun signUp(
        hashMap: HashMap<String, Any>,
        callBack: SignUpContract.CallBack
    ) {
        serviceGenerator.instance.signUp(hashMap).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callBack.onSignUpSuccess()
                } else {
                    callBack.onSignUpFailure()
                }
            }
        })
    }
}