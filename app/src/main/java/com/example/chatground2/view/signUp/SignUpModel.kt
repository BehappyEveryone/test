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
        listener: SignUpContract.Listener
    ) {
        serviceGenerator.instance.emailOverlap(hashMap).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onEmailOverlapSuccess()
                } else {
                    listener.onEmailOverlapFailure()
                }
            }
        })
    }

    //닉네임 중복 체크
    fun nicknameOverlap(
        hashMap: HashMap<String, Any>,
        listener: SignUpContract.Listener
    ) {
        serviceGenerator.instance.nicknameOverlap(hashMap)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        listener.onNicknameOverlapSuccess()
                    } else {
                        listener.onNicknameOverlapFailure()
                    }
                }
            })
    }

    //회원가입
    fun signUp(
        hashMap: HashMap<String, Any>,
        listener: SignUpContract.Listener
    ) {
        serviceGenerator.instance.signUp(hashMap).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onSignUpSuccess()
                } else {
                    listener.onSignUpFailure()
                }
            }
        })
    }
}