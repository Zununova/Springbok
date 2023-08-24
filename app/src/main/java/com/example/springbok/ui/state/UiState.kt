package com.example.springbok.ui.state

sealed class UiState<T> {

    class Success<T>(message: T) : UiState<T>()
    class Error<T>(message: T) : UiState<T>()
}