package com.example.chatground2.view.dialog

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.view.modifyForum.ModifyForumContract
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.PartMap

class CommentModifyModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //댓글 수정
    fun modifyComment(
        idx: String,
        id:String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        listener: CommentModifyContract.Listener
    ) {
        serviceGenerator.instance.modifyComment(idx,id, hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onModifyCommentSuccess()
                } else {
                    listener.onModifyCommentFailure()
                }
            }
        })
    }
}