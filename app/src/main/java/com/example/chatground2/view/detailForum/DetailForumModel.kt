package com.example.chatground2.view.detailForum

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.model.dto.ForumDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailForumModel (context: Context) {
    private val serviceGenerator: ServiceGenerator = ServiceGenerator(context)

    //포럼 자세히보기
    fun detailForum(
        idx: String,
        callBack: DetailForumContract.CallBack
    ) {
        serviceGenerator.instance.detailForum(idx)
            .enqueue(object : Callback<ForumDto?> {
                override fun onFailure(call: Call<ForumDto?>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<ForumDto?>,
                    response: Response<ForumDto?>
                ) {
                    if (response.isSuccessful) {
                        callBack.onDetailForumSuccess(response.body())
                    } else {
                        callBack.onDetailForumFailure()
                    }
                }
            })
    }

    //포럼 삭제
    fun deleteForum(
        idx: String,
        callBack: DetailForumContract.CallBack
    ) {
        serviceGenerator.instance.deleteForum(idx)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        callBack.onDeleteForumSuccess()
                    } else {
                        callBack.onDeleteForumFailure()
                    }
                }
            })
    }

    //포럼 추천
    fun recommendForum(
        idx: String,
        hashMap: HashMap<String, Any>,
        callBack: DetailForumContract.CallBack
    ) {
        serviceGenerator.instance.recommendForum(idx,hashMap)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callBack.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        callBack.onRecommendForumSuccess()
                    } else {
                        callBack.onRecommendForumFailure()
                    }
                }
            })
    }

    //댓글 쓰기
    fun writeComment(
        idx: String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        callBack: DetailForumContract.CallBack
    ) {
        serviceGenerator.instance.writeComment(idx,hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callBack.onWriteCommentSuccess()
                } else {
                    callBack.onWriteCommentFailure()
                }
            }
        })
    }

    //댓글 삭제
    fun deleteComment(
        idx: String,
        commentId:String,
        callBack: DetailForumContract.CallBack
    ) {
        serviceGenerator.instance.deleteComment(idx,commentId).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    callBack.onDeleteCommentSuccess()
                } else {
                    callBack.onDeleteCommentFailure()
                }
            }
        })
    }

}