package com.example.springbok.data

import com.example.springbok.R
import com.example.springbok.data.dto.ItemDto

class ItemData {

    fun setImage() = listOf(
        ItemDto(R.drawable.cub, 4),
        ItemDto(R.drawable.button, 30),
        ItemDto(R.drawable.coin1, 70),
        ItemDto(R.drawable.coin2, 100)
    )

}