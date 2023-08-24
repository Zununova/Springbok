package com.example.springbok.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.databinding.ActivityMainBinding
import com.example.springbok.ui.state.UiState

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind)
    private val viewModel by viewModels<WebViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSubscribes()
    }

    private fun setupSubscribes() {
        viewModel.data.observe(this) {
            when (it) {
                is UiState.Error -> {
                    binding.fragmentInMainActivity.isVisible = true
                    binding.webView.isGone = true
                }

                is UiState.Success -> {
                    connectWebView()
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun connectWebView() {
        binding.webView.isVisible = true
        binding.webView.webViewClient = WebViewClient()

        binding.webView.apply {
            viewModel.baseUrl.observe(this@MainActivity) {
                loadUrl(it)
            }
            settings.javaScriptEnabled = true

            if (binding.webView.canGoBack()) viewModel.updateUrl(binding.webView.url!!)
        }
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) binding.webView.goBack() else super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateUrl(binding.webView.url)
    }
}