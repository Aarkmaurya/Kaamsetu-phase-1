package com.nexora.kaamsetu.core.network

/** Generic success/failure wrapper for an operation that can fail with an [AppError]. */
sealed interface NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>
    data class Failure(val error: AppError) : NetworkResult<Nothing>
}
