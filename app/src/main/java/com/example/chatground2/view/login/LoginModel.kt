package com.example.chatground2.view.login

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.model.dto.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel(context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //로그인
    fun signIn(
        hashMap: HashMap<String, Any>,
        callBack: LoginContract.CallBack
    ) {
        serviceGenerator.instance.signIn(hashMap).enqueue(object : Callback<UserDto> {
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { callBack.onLoginSuccess(it) }
                } else {
                    callBack.onLoginFailure()
                }
            }
        })
    }
}