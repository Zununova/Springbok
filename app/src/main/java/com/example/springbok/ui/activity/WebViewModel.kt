package com.example.springbok.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.springbok.data.repositories.WebRepository
import com.example.springbok.ui.constants.Constants
import com.example.springbok.ui.state.UiState

class WebViewModel : ViewModel() {

    private val repository = WebRepository()
    private val _data = MutableLiveData<UiState<String?>>()
    val data: LiveData<UiState<String?>> = _data

    private val _baseUrl = MutableLiveData(Constants.BASE_URL)
    val baseUrl: LiveData<String> = _baseUrl

    init {
        fetchData()
    }

    fun updateUrl(baseUrl: String?) {
        _baseUrl.value = baseUrl
    }

    private fun fetchData() {
        repository.fetchData { result ->
            if (result.isSuccess) {
                _data.postValue(UiState.Success(result.getOrNull()))
            } else {
                _data.postValue(UiState.Error(result.getOrNull()))
            }
        }
    }
}