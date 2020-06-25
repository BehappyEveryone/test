package com.example.chatground2.view.modifyForum

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyForumModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //포럼 수정
    fun modifyForum(
        idx: String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: Array<MultipartBody.Part?>,
        listener: ModifyForumContract.Listener
    ) {
        serviceGenerator.instance.modifyForum(idx, hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onModifySuccess()
                } else {
                    listener.onModifyFailure()
                }
            }
        })
    }
}