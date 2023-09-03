package com.example.springbok.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.databinding.ActivityMainBinding
import com.example.springbok.ui.constants.Constants.CAMERA_REQUEST_CODE
import com.example.springbok.ui.state.UiState
import com.example.springbok.ui.webviewclient.MyWebViewClient
import com.example.springbok.ui.webviewclient.WebViewInterface
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModels<WebViewModel>()
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var contract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        filePathCallback?.onReceiveValue(arrayOf(imageUri))
        viewModel.updateImageUri(imageUri)
    }
    private lateinit var imageUri: Uri
    private lateinit var currentPhotoPath: String
    private val okHttpClient = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSubscribes()
    }

    private fun setupSubscribes() {
        lifecycleScope.launch {
            viewModel.data.observe(this@MainActivity) {
                when (it) {
                    is UiState.Error -> {
                        binding.fragmentInMainActivity.isVisible = true
                        binding.webView.isGone = true
                    }

                    is UiState.Success -> {
                        connectWebView()
                        Log.e(TAG, it.toString())
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun connectWebView() = with(binding.webView) {
        lifecycleScope.launch {
            isVisible = true
            webViewClient = MyWebViewClient()
            webChromeClient = object : WebChromeClient() {

                // Open Camera
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    this@MainActivity.filePathCallback = filePathCallback
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_REQUEST_CODE
                        )
                    } else {
                        openCamera()
                    }
                    return true
                }
            }
            settings.javaScriptEnabled = true
            settings.allowContentAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.domStorageEnabled = true
            evaluateJavascript("(function() { return document.documentElement.innerHTML; })();") { result ->
                sendDataToServer(result ?: "")
            }

            addJavascriptInterface(WebViewInterface(this@MainActivity), "Android")
            viewModel.baseUrl.observe(this@MainActivity) {
                loadUrl(it)
            }
        }
    }

    private fun sendDataToServer(content: String) {
        val client = OkHttpClient()
        val url = binding.webView.url.toString()
        val requestBody = FormBody.Builder()
            .add("content", content)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: okio.IOException) {
                Log.e(TAG, "onFailure: ", )
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e(TAG, "onResponse: ", )
            }
        })
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageArray = arrayOf(imageUri)
            filePathCallback?.onReceiveValue(imageArray)
            filePathCallback = null
        }
    }

    private fun openCamera() {
        imageUri = createImageFile()!!
        contract.launch(imageUri)
    }

    private fun createImageFile(): Uri? {
        val imageFile = File(applicationContext.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.springbok.fileProvider",
            imageFile
        )
    }
}