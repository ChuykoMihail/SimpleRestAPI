package com.example.myapplication.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class ApiRepository {
    private val TAG = "ApiRepository"
    var token: String = "Bearer "
    val todoApi: TodoApi
    val myRequestBody: MyRequestBody

    init {
        val retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("http://165.22.93.105:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        todoApi = retrofit.create(TodoApi::class.java)
        myRequestBody = MyRequestBody("", "", "", "")
    }


    // Функция логина пользователя. Принимает на вход почту и пароль,
    // возвращает объект класса MyResponse, в котором содержится токен.
    // Выводит в логи сообщение о полученном токене.
    fun loginUser(email: String, password: String):LiveData<MyResponseBody>{

        myRequestBody.apply {
            this.email = email
            this.password = password
        }

        val responseLiveData:MutableLiveData<MyResponseBody> = MutableLiveData()
        val loginRequest: Call<MyResponseBody> = todoApi.loginUser(myRequestBody)



        loginRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                    call: Call<MyResponseBody>,
                    response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Login onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                    token += response.body()?.token!!
                    Log.d(TAG, "Login onResponse token = ${token}")
                    newTask("Getting Over it")
                } else {
                    Log.d(TAG, "Login onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "Login network error")
            }
        })
        return responseLiveData
    }

    // Функция регистрации нового пользователя.
    // Принимает на вход имейл и пароль нового пользователя.
    // Возвращает ответ сервера на запрос в формате MyResponseBody
    fun registerUser(email: String, password: String):LiveData<MyResponseBody>{

        myRequestBody.apply {
            this.email = email
            this.password = password
        }
        val responseLiveData:MutableLiveData<MyResponseBody> = MutableLiveData()
        val registerRequest: Call<MyResponseBody> = todoApi.registerUser(myRequestBody)

        registerRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                    call: Call<MyResponseBody>,
                    response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Register onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                } else {
                    Log.d(TAG, "Register onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "Register network error")
                if (t is IOException) {
                    Log.d(TAG, "${t.message}");
                    // logging probably not necessary
                } else {
                    Log.d(TAG, "conversion issue! big problems :(");
                    // todo log to some central bug tracking service
                }
            }

        })

        return responseLiveData
    }

    // Функция получения списка задач.
    // Ничего не принемает на вход. Требует наличия токена.
    // Возвращает ответ сервера на запрос в формате MyResponseBody
    // содержащее List<Task>
    fun getTaskList():LiveData<MyResponseBody>{
        val responseLiveData:MutableLiveData<MyResponseBody> = MutableLiveData()
        val taskListRequest:Call<MyResponseBody> = todoApi.getTasks(token)

        taskListRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                    call: Call<MyResponseBody>,
                    response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "getTaskList onResponse isSuccessful = true")
                    Log.d(TAG, "getTaskList body ${response.body()?.items.toString()}")
                    responseLiveData.value = response.body()
                    response.body()?.items?.get(0)?.let { updateTask(it.id,"Defenitely getting over it") }
                } else {
                    Log.d(TAG, "getTaskList onResponse isSuccessful = true")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "getTaskList onFailure")
            }
        })
        return responseLiveData
    }

    // Функция обновления текста задачи.
    // Принимает на вход новый текст задачи.
    // Возвращает ответ сервера на запрос в формате MyResponseBody
    fun newTask(newTaskText: String): LiveData<MyResponseBody>{

        myRequestBody.apply {
            this.taskText = newTaskText
        }

        val responseLiveData : MutableLiveData<MyResponseBody> = MutableLiveData()
        val newTaskRequest: Call<MyResponseBody> = todoApi.postNewTask(myRequestBody, token)

        newTaskRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(
                    call: Call<MyResponseBody>,
                    response: Response<MyResponseBody>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "NewTask onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                    getTaskList()
                } else {
                    Log.d(TAG, "NewTask onResponse isSuccessful = false ${response.errorBody()!!.charStream().readText()}")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "NewTask onFailure")
            }
        })
        return responseLiveData
    }

    // Функция обновления текста задачи
    // принимает на вход ID задачи и новый текст
    // Возвращает ответ сервера на запрос в формате MyResponseBody
    fun updateTask(id: Int, newTaskText: String):LiveData<MyResponseBody>{
        myRequestBody.apply {
            this.newText = newTaskText
        }

        val responseLiveData : MutableLiveData<MyResponseBody> = MutableLiveData()
        val updateTaskRequest: Call<MyResponseBody> = todoApi.updateTask(id, myRequestBody, token)

        updateTaskRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(call: Call<MyResponseBody>, response: Response<MyResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "UpdateTask onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                    Log.d(TAG, "UpdateTask onresponse ${response.body()?.message}")
                    deleteTask(id)
                } else {
                    Log.d(TAG, "UpdateTask onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "UpdateTask onFailure")
            }
        })
        return responseLiveData
    }

    // Функция удаления задачи
    // принимает на вход id задачи
    // возвращает ответ сервера виде экземпляра MyResponseBody
    fun deleteTask(id: Int):LiveData<MyResponseBody>{
        val responseLiveData : MutableLiveData<MyResponseBody> = MutableLiveData()
        val deleteTaskRequest: Call<MyResponseBody> = todoApi.deleteTask(id, token)

        deleteTaskRequest.enqueue(object : Callback<MyResponseBody> {
            override fun onResponse(call: Call<MyResponseBody>, response: Response<MyResponseBody>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "UpdateTask onResponse isSuccessful = true")
                    responseLiveData.value = response.body()
                    Log.d(TAG, "DeleteTask ${response.body()?.message}")
                } else {
                    Log.d(TAG, "UpdateTask onResponse isSuccessful = false")
                }
            }

            override fun onFailure(call: Call<MyResponseBody>, t: Throwable) {
                Log.d(TAG, "UpdateTask onFailure")
            }
        })
        return responseLiveData
    }

}