package com.example.chatground2.view.writeForum

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WriteForumModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //포럼 쓰기
    fun writeForum(
        hashMap: HashMap<String, RequestBody>,
        imagePart: Array<MultipartBody.Part?>,
        callBack: WriteForumContract.CallBack
    ) {
        serviceGenerator.instance.writeForum(hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callBack.onSuccess()
                } else {
                    callBack.onFailure()
                }
            }
        })
    }
}