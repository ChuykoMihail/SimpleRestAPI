package com.example.myapplication.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TodoApi {

    @POST("login")
    fun loginUser(@Body body: MyRequestBody) : Call<MyResponseBody>

    @POST("register")
    fun registerUser(@Body body:MyRequestBody) : Call<MyResponseBody>

    @GET("files/{id}")
    fun downloadFile(@Path("id") id:Int): Call<ResponseBody?>?

    @Multipart
    @POST("files")
    fun uploadFile(
        @Part("description") descriptiom : RequestBody,
        @Part file : MultipartBody.Part
    )


    @GET("todo")
    fun getTasks(@Header("Authorization") authorization: String ) : Call<MyResponseBody>

    @POST("todo")
    fun postNewTask(@Body body:MyRequestBody, @Header("Authorization") authorization: String ) : Call<MyResponseBody>

    @PUT("todo/{id}")
    fun updateTask(@Path("id") id:Int, @Body body:MyRequestBody, @Header("Authorization") authorization: String ) : Call<MyResponseBody>

    @DELETE("todo/{id}")
    fun deleteTask(@Path("id") id:Int, @Header("Authorization") authorization: String ) : Call<MyResponseBody>
}