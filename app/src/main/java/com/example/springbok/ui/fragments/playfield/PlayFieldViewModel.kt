package com.example.springbok.ui.fragments.playfield

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayFieldViewModel : ViewModel() {

    private var _lifeCount = MutableLiveData(2)
    val lifeCount: LiveData<Int> = _lifeCount

    private var _coinCount = MutableLiveData(0)
    val coinCount: LiveData<Int> = _coinCount

    fun minusLifeCount() {
        _lifeCount.value = _lifeCount.value?.minus(1)
    }

    fun addCoin() {
        _coinCount.value = _coinCount.value?.plus(1)
    }

    fun plusLifeCount() {
        _lifeCount.value = _lifeCount.value?.plus(1)
    }

    fun restartGame() {
        _lifeCount.value = 2
        _coinCount.value = 0
    }
}