package com.komputerkit.realtimeweatherapp.data.model

sealed class WeatherResult<out T> {
    object Loading : WeatherResult<Nothing>()
    
    data class Success<T>(val data: T) : WeatherResult<T>()
    
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "Unknown error occurred"
    ) : WeatherResult<Nothing>()
    
    companion object {
        fun <T> loading(): WeatherResult<T> = Loading
        fun <T> success(data: T): WeatherResult<T> = Success(data)
        fun <T> error(exception: Throwable, message: String? = null): WeatherResult<T> = 
            Error(exception, message ?: exception.message ?: "Unknown error occurred")
    }
}

// Extension functions untuk handling hasil
inline fun <T> WeatherResult<T>.onLoading(action: () -> Unit): WeatherResult<T> {
    if (this is WeatherResult.Loading) action()
    return this
}

inline fun <T> WeatherResult<T>.onSuccess(action: (T) -> Unit): WeatherResult<T> {
    if (this is WeatherResult.Success) action(data)
    return this
}

inline fun <T> WeatherResult<T>.onError(action: (Throwable, String) -> Unit): WeatherResult<T> {
    if (this is WeatherResult.Error) action(exception, message)
    return this
}

fun <T> WeatherResult<T>.dataOrNull(): T? = when (this) {
    is WeatherResult.Success -> data
    else -> null
}

fun <T> WeatherResult<T>.isLoading(): Boolean = this is WeatherResult.Loading

fun <T> WeatherResult<T>.isSuccess(): Boolean = this is WeatherResult.Success

fun <T> WeatherResult<T>.isError(): Boolean = this is WeatherResult.Error