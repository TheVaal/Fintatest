package com.vaalzebub.fintatechtest.domain.usecase

import com.vaalzebub.fintatechtest.data.mappers.toModel
import com.vaalzebub.fintatechtest.domain.repository.ApiRepository
import com.vaalzebub.fintatechtest.domain.utils.ApiResponse
import com.vaalzebub.fintatechtest.domain.utils.SessionManager
import com.vaalzebub.fintatechtest.domain.utils.onError
import com.vaalzebub.fintatechtest.domain.utils.onSuccess
import kotlinx.coroutines.flow.flow

class ApiUseCase(
    private val repository: ApiRepository,
    private val manager: SessionManager
) {
    /*
    * send auth request to api if current token isn't valid
    */
    fun authorize()= flow {
        if(!isTokenValid()){
            emit(repository.authorize())
        } else {
            emit(ApiResponse.Success(true))
        }
    }

    fun getInstruments() = flow{
        repository.getInstruments().onSuccess {result->
            emit(
                ApiResponse.Success(
                    result.map { item-> item.toModel() }
                )
            )
        }.onError{
            emit(ApiResponse.Error(it.toString()))
        }
    }

    fun getPrices(instrumentId: String) = flow {
        repository.getPrices(instrumentId).onSuccess{
            emit(
                ApiResponse.Success(
                    it.map { item-> item.toModel() }
                )
            )
        }.onError{
            emit(ApiResponse.Error(it.toString()))
        }
    }

    /*
    * check if current token still usable
    */
    private fun isTokenValid(): Boolean{
        val currentTime = System.currentTimeMillis()
        val expireTime = manager.fetchExpiration()
        return currentTime<=expireTime
    }
}