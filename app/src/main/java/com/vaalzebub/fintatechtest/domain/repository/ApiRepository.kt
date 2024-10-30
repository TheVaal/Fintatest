package com.vaalzebub.fintatechtest.domain.repository

import com.vaalzebub.fintatechtest.data.FinApi
import com.vaalzebub.fintatechtest.data.source.HistoryPriceDto
import com.vaalzebub.fintatechtest.data.source.ItemDto
import com.vaalzebub.fintatechtest.domain.utils.ApiResponse
import com.vaalzebub.fintatechtest.domain.utils.SessionManager
import retrofit2.Response

class ApiRepository(private val finApi: FinApi, private val manager: SessionManager) {
    /*
    * retrieve token from [FinApi] and save to prefs through [SessionManager]
    */
    suspend fun authorize(): ApiResponse<Boolean> {
        return wrapResponse(
            response = finApi.getToken(),
            errorMessage = "Authorization server is not responding"
        ) { value ->
            manager.saveAuthToken(value.token, value.expiresIn)
            true
        }


    }

    suspend fun getInstruments(): ApiResponse<List<ItemDto>> {
        return wrapResponse(
            response = finApi.getInstruments(),
            errorMessage = "Unable to get instrument from remote server"
        ) { value ->
            value.items
        }
    }

    suspend fun getPrices(instrumentId: String): ApiResponse<List<HistoryPriceDto>> {
        val error = "Unable to get prices from remote server"
        val response = wrapResponse(
            response = finApi.getPrices(instrumentId),
            errorMessage = error,
        ) { value ->
            value.items
        }
        return response

    }

}

private fun <T, R> wrapResponse(
    response: Response<T>,
    errorMessage: String,
    onSuccess: (T) -> R
): ApiResponse<R> {
    return try {
        if (response.isSuccessful) {
            val responseData = response.body()
            if (responseData != null) {
                ApiResponse.Success(onSuccess(responseData))
            } else {
                ApiResponse.Error(errorMessage)
            }
        } else {
            ApiResponse.Error(errorMessage)
        }
    } catch (e: Exception) {
        return ApiResponse.Error(errorMessage)
    }
}
