package com.example.springbok.ui.webviewclient

import android.content.Context
import android.webkit.JavascriptInterface
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class WebViewInterface(private val context: Context) {

    @JavascriptInterface
    fun sendDataForServer(data: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("data", data)
            .build()
        val request = Request.Builder()
            .url("YOUR_SERVER_URL")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка успешного ответа от сервера
            }
        })
    }
}