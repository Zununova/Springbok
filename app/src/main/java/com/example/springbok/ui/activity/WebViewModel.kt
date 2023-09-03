package com.example.springbok.ui.activity

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.springbok.data.repositories.WebRepository
import com.example.springbok.ui.constants.Constants
import com.example.springbok.ui.state.UiState
import kotlinx.coroutines.launch

class WebViewModel : ViewModel() {

    private val repository = WebRepository()
    private val _data = MutableLiveData<UiState<String?>>()
    val data: LiveData<UiState<String?>> = _data

    private val _baseUrl = MutableLiveData(Constants.BASE_URL)
    val baseUrl: LiveData<String> = _baseUrl

    private val _imageUrl = MutableLiveData<Uri>()
    val imageUrl: LiveData<Uri> = _imageUrl

    init {
        fetchData()
    }

    fun updateImageUri(imageUri: Uri){
        _imageUrl.value = imageUri
    }

    fun updateUrl(baseUrl: String?) {
        viewModelScope.launch {
            _baseUrl.value = baseUrl
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            repository.fetchData { result ->
                if (result.isSuccess) {
                    _data.postValue(UiState.Success(result.getOrNull()))
                } else {
                    _data.postValue(UiState.Error(result.getOrNull()))
                }
            }
        }
    }
}