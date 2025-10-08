package com.komputerkit.realtimeweatherapp.data.network

import com.komputerkit.realtimeweatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("aqi") includeAqi: String = "no"
    ): Response<WeatherResponse>
    
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
    }
}