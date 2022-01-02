package com.example.myapplication.api

import com.google.gson.annotations.SerializedName

data class MyRequestBody(
    @SerializedName("email")
    var email: String,
    @SerializedName("new_text")
    var newText: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("taskText")
    var taskText: String

)