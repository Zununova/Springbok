package com.example.springbok.data.repositories

import com.example.springbok.ui.constants.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebRepository {

    fun fetchData(callback: (Result<String>) -> Unit) {
        val request = Request.Builder()
            .url(Constants.BASE_URL)
            .build()
        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 404) {
                    callback(Result.failure(IOException(response.message)))
                } else {
                    callback(Result.success(response.message))
                }
            }
        })
    }
}