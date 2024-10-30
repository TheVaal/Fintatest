package com.vaalzebub.fintatechtest.data.source

import com.google.gson.annotations.SerializedName

data class ItemDto(

    @SerializedName("id")
    val id: String,
    @SerializedName("symbol")
    val name: String
)