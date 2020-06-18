package com.example.chatground2.api

import com.example.chatground2.model.dto.ForumDto
import com.example.chatground2.model.dto.UserDto
import com.example.chatground2.model.dto.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    //이메일 중복 체크
    @GET("/auth/email")
    fun emailOverlap(
        @QueryMap hashMap: HashMap<String, Any>
    ): Call<ResponseBody>

    //이메일 중복 체크
    @GET("/auth/nickname")
    fun nicknameOverlap(
        @QueryMap hashMap: HashMap<String, Any>
    ): Call<ResponseBody>

    //회원가입
    @FormUrlEncoded
    @POST("/users")
    fun signUp(
        @FieldMap hashMap: HashMap<String, Any>
    ): Call<ResponseBody>

    //로그인
    @FormUrlEncoded
    @POST("/auth/login")
    fun signIn(
        @FieldMap hashMap: HashMap<String, Any>
    ): Call<UserDto>

    //포럼 불러오기
    @GET("/forums")
    fun callForums(
        @QueryMap hashMap: HashMap<String, Any>
    ): Call<ArrayList<ForumDto>?>

    //포럼 자세히 보기
    @GET("/forums/{idx}")
    fun detailForum(
        @Path("idx") idx: String
    ): Call<ForumDto?>

    //포럼 만들기
    @Multipart
    @POST("/forums")
    fun writeForum(
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part imagePart: Array<MultipartBody.Part?>
    ): Call<ResponseBody>

    //포럼 지우기
    @DELETE("/forums/{idx}")
    fun deleteForum(
        @Path("idx") idx: String
    ): Call<ResponseBody>

    //포럼 수정
    @Multipart
    @PATCH("/forums/{idx}")
    fun modifyForum(
        @Path("idx") idx: String,
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part imagePart: Array<MultipartBody.Part?>
    ):Call<ResponseBody>

    //댓글 쓰기
    @Multipart
    @POST("/forums/{idx}/comments")
    fun writeComment(
        @Path("idx") idx: String,
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part imagePart: MultipartBody.Part?
    ): Call<ResponseBody>

    //댓글 지우기
    @DELETE("/forums/{idx}/comments/{id}")
    fun deleteComment(
        @Path("idx") idx: String,
        @Path("id") commentId: String
    ): Call<ResponseBody>

    //추천하기
    @FormUrlEncoded
    @POST("/forums/{idx}/recommend")
    fun recommendForum(
        @Path("idx") idx: String,
        @FieldMap hashMap: HashMap<String, Any>
    ): Call<ResponseBody>

    //유저 조회
    @GET("/users/{email}")
    fun callUser(
        @Path("email") email: String
    ): Call<UserDto>

    //프로필 수정
    @Multipart
    @PATCH("/users/{email}")
    fun modifyProfile(
        @Path("email") email: String,
        @PartMap hashMap: HashMap<String, RequestBody>,
        @Part imagePart: MultipartBody.Part?
    ): Call<ResponseBody>
}