package com.vaalzebub.fintatechtest.data.source

import com.google.gson.annotations.SerializedName

data class HistoryPriceDto(

    @SerializedName("t")
    val time:String,

    @SerializedName("o")
    val open:Double,

    @SerializedName("h")
    val high:Double,

    @SerializedName("l")
    val low:Double,

    @SerializedName("c")
    val close:Double,

    @SerializedName("v")
    val volume:Double,
)
