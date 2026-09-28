package com.charan.yourday.utils

sealed class ProcessState<out T> {
    object NotDetermined : ProcessState<Nothing>()
    data class Loading(
        val progress : Float? = null
    ) : ProcessState<Nothing>()
    data class Success<T>(val data: T) : ProcessState<T>()
    data class Streaming<T>(val partialData: T) : ProcessState<T>()
    data class Error(val message: String) : ProcessState<Nothing>()

    fun isLoading() : Boolean {
        return this is Loading
    }

    fun extractData() : T? {
        return (this as? Success<T>)?.data
    }
    fun getError() : String? {
        return (this as? Error)?.message
    }
}
