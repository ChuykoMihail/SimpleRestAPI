package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.myapplication.R
import com.example.myapplication.api.ApiRepository
import com.example.myapplication.api.MyResponseBody

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var mainToken = "Bearer "


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiRepository = ApiRepository()

        //apiRepository.registerUser("myemail@gmail.com", "qwerty123456")

        val todoLiveData: LiveData<MyResponseBody> = apiRepository.loginUser("myemail@gmail.com", "qwerty123456")
        todoLiveData.observe(
            this,
            Observer { mResponseBody ->
                mainToken += mResponseBody.token
            }
        )
//        apiRepository.newTask("I have to work for food")
//        apiRepository.getTaskList()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"Token is ${mainToken}")
    }
}