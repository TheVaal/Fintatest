package com.vaalzebub.fintatechtest.data.source

import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("access_token")
    val token: String,
    @SerializedName("expires_in")
    val expiresIn: Int,

)


