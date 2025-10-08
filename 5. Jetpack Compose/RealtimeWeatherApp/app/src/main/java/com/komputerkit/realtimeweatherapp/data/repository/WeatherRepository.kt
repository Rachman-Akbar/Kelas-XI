package com.komputerkit.realtimeweatherapp.data.repository

import android.util.Log
import com.komputerkit.realtimeweatherapp.data.model.WeatherResponse
import com.komputerkit.realtimeweatherapp.data.model.WeatherResult
import com.komputerkit.realtimeweatherapp.data.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val apiKey: String
) {
    
    suspend fun getCurrentWeather(location: String): WeatherResult<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (location.isBlank()) {
                    return@withContext WeatherResult.error<WeatherResponse>(
                        IllegalArgumentException("Location cannot be empty"),
                        "Please enter a valid city name"
                    )
                }
                
                val response = weatherApiService.getCurrentWeather(
                    apiKey = apiKey,
                    location = location.trim()
                )
                
                when {
                    response.isSuccessful -> {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            WeatherResult.success(weatherData)
                        } else {
                            WeatherResult.error<WeatherResponse>(
                                RuntimeException("Empty response from server"),
                                "No weather data found for this location"
                            )
                        }
                    }
                    else -> {
                        val errorMessage = when (response.code()) {
                            400 -> "Invalid location. Please check the city name."
                            401 -> "API key is invalid. Please check your configuration."
                            403 -> "Access denied. API quota might be exceeded."
                            404 -> "Location not found. Please try a different city name."
                            429 -> "Too many requests. Please try again later."
                            500, 502, 503 -> "Weather service is temporarily unavailable."
                            else -> "Failed to get weather data (Code: ${response.code()})"
                        }
                        
                        WeatherResult.error<WeatherResponse>(
                            HttpException(response),
                            errorMessage
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.e("WeatherRepository", "Error fetching weather data", exception)
                
                val errorMessage = when (exception) {
                    is UnknownHostException -> "No internet connection. Please check your network."
                    is ConnectException -> "Cannot connect to weather service. Please try again."
                    is SocketTimeoutException -> "Request timed out. Please check your connection."
                    is IOException -> "Network error occurred. Please try again."
                    is HttpException -> {
                        when (exception.code()) {
                            401 -> "Invalid API key configuration"
                            403 -> "API access denied"
                            404 -> "Location not found"
                            429 -> "Too many requests. Please wait a moment."
                            else -> "Server error (${exception.code()})"
                        }
                    }
                    else -> "An unexpected error occurred: ${exception.message}"
                }
                
                WeatherResult.error(exception, errorMessage)
            }
        }
    }
    
    /**
     * Validates location input before making API call
     */
    fun validateLocation(location: String): String? {
        return when {
            location.isBlank() -> "Location cannot be empty"
            location.length < 2 -> "Location must be at least 2 characters"
            location.length > 100 -> "Location name is too long"
            location.matches(Regex(".*\\d.*")) && !location.matches(Regex(".*[a-zA-Z].*")) -> 
                "Please enter a valid city name"
            else -> null
        }
    }
}