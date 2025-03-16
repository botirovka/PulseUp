package com.example.domain.models

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val message: String) : Response<Nothing>()

    object WrongPassword : Response<Nothing>()
    object LinkToGoogle : Response<Nothing>()
    object EmailNotFound : Response<Nothing>()
    object EmailAlreadyInUse : Response<Nothing>()
}