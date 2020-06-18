package com.example.chatground2.model.dao

import android.content.Context
import com.example.chatground2.api.ServiceGenerator
import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.model.dto.UserDto
import com.example.chatground2.model.dto.DefaultResponse
import com.example.chatground2.view.login.LoginContract
import com.example.chatground2.view.signUp.SignUpContract
import com.example.chatground2.view.detailForum.DetailForumContract
import com.example.chatground2.view.forums.ForumsContract
import com.example.chatground2.view.modifyForum.ModifyForumContract
import com.example.chatground2.view.profile.ProfileContract
import com.example.chatground2.view.writeForum.WriteForumContract
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class Model(context: Context) {
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

    //로그인
    fun signIn(
        hashMap: HashMap<String, Any>,
        listener: LoginContract.Listener
    ) {
        serviceGenerator.instance.signIn(hashMap).enqueue(object : Callback<UserDto> {
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    response.body()?.let { listener.onLoginSuccess(it) }
                } else {
                    listener.onLoginFailure()
                }
            }
        })
    }

    //포럼 목록
    fun callForums(
        hashMap: HashMap<String, Any>,
        listener: ForumsContract.Listener
    ) {
        serviceGenerator.instance.callForums(hashMap)
            .enqueue(object : Callback<ArrayList<ForumDto>?> {
                override fun onFailure(call: Call<ArrayList<ForumDto>?>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<ArrayList<ForumDto>?>,
                    response: Response<ArrayList<ForumDto>?>
                ) {
                    if (response.isSuccessful) {
                        listener.onCallForumsSuccess(response.body())
                    } else {
                        listener.onCallForumsFailure()
                    }
                }
            })
    }

    //포럼 자세히보기
    fun detailForum(
        idx: String,
        listener: DetailForumContract.Listener
    ) {
        serviceGenerator.instance.detailForum(idx)
            .enqueue(object : Callback<ForumDto?> {
                override fun onFailure(call: Call<ForumDto?>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<ForumDto?>,
                    response: Response<ForumDto?>
                ) {
                    if (response.isSuccessful) {
                        listener.onDetailForumSuccess(response.body())
                    } else {
                        listener.onDetailForumFailure()
                    }
                }
            })
    }

    //포럼 쓰기
    fun writeForum(
        hashMap: HashMap<String, RequestBody>,
        imagePart: Array<MultipartBody.Part?>,
        listener: WriteForumContract.Listener
    ) {
        serviceGenerator.instance.writeForum(hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onSuccess()
                } else {
                    listener.onFailure()
                }
            }
        })
    }

    //포럼 삭제
    fun deleteForum(
        idx: String,
        listener: DetailForumContract.Listener
    ) {
        serviceGenerator.instance.deleteForum(idx)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        listener.onDeleteForumSuccess()
                    } else {
                        listener.onDeleteForumFailure()
                    }
                }
            })
    }

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

    //포럼 추천
    fun recommendForum(
        idx: String,
        hashMap: HashMap<String, Any>,
        listener: DetailForumContract.Listener
    ) {
        serviceGenerator.instance.recommendForum(idx,hashMap)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        listener.onRecommendForumSuccess()
                    } else {
                        listener.onRecommendForumFailure()
                    }
                }
            })
    }

    //댓글 쓰기
    fun writeComment(
        idx: String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        listener: DetailForumContract.Listener
    ) {
        serviceGenerator.instance.writeComment(idx,hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onWriteCommentSuccess()
                } else {
                    listener.onWriteCommentFailure()
                }
            }
        })
    }

    //댓글 삭제
    fun deleteComment(
        idx: String,
        commentId:String,
        listener: DetailForumContract.Listener
    ) {
        serviceGenerator.instance.deleteComment(idx,commentId).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    listener.onDeleteCommentSuccess()
                } else {
                    listener.onDeleteCommentFailure()
                }
            }
        })
    }

    //유저 조회
    fun callUser(
        email: String,
        listener: ProfileContract.Listener
    ) {
        serviceGenerator.instance.callUser(email)
            .enqueue(object : Callback<UserDto> {
                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    listener.onError(t)
                }

                override fun onResponse(
                    call: Call<UserDto>,
                    response: Response<UserDto>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { listener.onCallUserSuccess(it) }
                    } else {
                        listener.onCallUserFailure()
                    }
                }
            })
    }

    //프로필 수정
    fun modifyProfile(
        email:String,
        hashMap: HashMap<String, RequestBody>,
        imagePart: MultipartBody.Part?,
        listener: ProfileContract.Listener
    ) {
        serviceGenerator.instance.modifyProfile(email,hashMap, imagePart).enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onSaveSuccess()
                } else {
                    listener.onSaveFailure()
                }
            }
        })
    }
}