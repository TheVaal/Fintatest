package com.vaalzebub.fintatechtest.data.source

import com.google.gson.annotations.SerializedName

data class DataWrapper<T>(
    @SerializedName("data")
    val items: List<T>
)


