package br.com.unb.smms

sealed class SmmsData<out T: Any> {
    data class Success<out T: Any>(val data: T): SmmsData<T>()
    data class Error(val error: Throwable): SmmsData<Nothing>()
}