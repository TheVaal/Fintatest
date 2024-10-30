package com.vaalzebub.fintatechtest.domain.utils

import android.content.SharedPreferences

class SessionManager (private val prefs: SharedPreferences) {

    private val userToken = "user_token"
    private val expiration = "tokenExpiration"

    /**
     * Function to save auth token and token expiration millis
    */
    fun saveAuthToken(token: String, expiresIn:Int) {
        val millis = System.currentTimeMillis()
        prefs.edit()
            .putString(userToken, token)
            .putLong(expiration, millis + (expiresIn * 1000))
            .apply()
    }

    /**
     * Function to fetch auth token
    */
    fun fetchAuthToken(): String? {
        return prefs.getString(userToken, null)
    }

    /**
     * Function to fetch token expiration millis
    */
    fun fetchExpiration(): Long {
        return prefs.getLong(expiration, 0)
    }
}