package com.example.myapplication.api

import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("id") val id : Int,
    @SerializedName("text") val text : String
)
