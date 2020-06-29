package com.example.chatground2.view.dialog

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentModifyModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //댓글 수정
    fun modifyComment(
        idx: String,
        id:String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        callBack: CommentModifyContract.CallBack
    ) {
        serviceGenerator.instance.modifyComment(idx,id, hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callBack.onModifyCommentSuccess()
                } else {
                    callBack.onModifyCommentFailure()
                }
            }
        })
    }
}