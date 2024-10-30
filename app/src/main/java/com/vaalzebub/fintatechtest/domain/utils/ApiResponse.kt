package com.vaalzebub.fintatechtest.domain.utils
sealed interface ApiResponse<out T> {
    class Success<T>(val data: T) : ApiResponse<T>
    class Error(val errorMessage: String) : ApiResponse<Nothing>


}

inline fun <R> ApiResponse<R>.onSuccess(block: (R) -> Unit): ApiResponse<R> {
    if (this is ApiResponse.Success) {
        block(data)
    }
    return this
}

inline fun <R> ApiResponse<R>.onError(block: (exception: String?) -> Unit): ApiResponse<R> {
    if (this is ApiResponse.Error) {
        block(errorMessage)
    }
    return this
}
