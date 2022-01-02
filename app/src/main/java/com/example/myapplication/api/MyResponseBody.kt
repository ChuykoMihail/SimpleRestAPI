package com.example.myapplication.api

import com.google.gson.annotations.SerializedName


data class MyResponseBody(
    @SerializedName("message")  var message: String,
    @SerializedName("code") var code: String,
    @SerializedName("error") var error: String,
    @SerializedName("token") var token: String,
    @SerializedName("items") var items: MutableList<Task>,
    @SerializedName("task_text") var newTaskText: String
)
