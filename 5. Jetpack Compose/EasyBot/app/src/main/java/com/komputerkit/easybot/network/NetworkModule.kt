package com.komputerkit.easybot.network

import com.google.gson.GsonBuilder
import com.komputerkit.easybot.config.OpenAIConfig
import com.komputerkit.easybot.data.api.OpenAIService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private val gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (OpenAIConfig.DEBUG_MODE) {
            HttpLoggingInterceptor.Level.BASIC // Untuk debugging
        } else {
            HttpLoggingInterceptor.Level.NONE // Untuk production
        }
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Meningkatkan timeout untuk koneksi yang lambat
        .readTimeout(60, TimeUnit.SECONDS)    // Timeout untuk membaca respons yang lebih lama
        .writeTimeout(30, TimeUnit.SECONDS)   // Timeout untuk menulis request
        .retryOnConnectionFailure(true)       // Retry otomatis pada connection failure
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(OpenAIConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val openAIService: OpenAIService = retrofit.create(OpenAIService::class.java)
}