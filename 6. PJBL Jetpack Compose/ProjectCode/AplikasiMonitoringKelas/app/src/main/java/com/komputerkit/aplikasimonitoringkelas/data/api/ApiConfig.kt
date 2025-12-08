package com.komputerkit.aplikasimonitoringkelas.data.api

import com.komputerkit.aplikasimonitoringkelas.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    // Fixed Base URL: use the project's API server IP (non-dynamic)
    // Use this when you want to connect to the development server directly
    // Example: http://10.54.122.126:8000/api/
    private const val BASE_URL = "http://10.54.122.126:8000/api/"

    init {
        android.util.Log.d("ApiConfig", "=== API CONFIGURATION ===")
        android.util.Log.d("ApiConfig", "BASE_URL: $BASE_URL")
        android.util.Log.d("ApiConfig", "DEBUG Mode: ${BuildConfig.DEBUG}")
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE  // No logging in production for performance
        }
    }

    private val customInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder()
            // Add common headers for all requests
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()

        android.util.Log.d("ApiConfig", "=== API REQUEST ===")
        android.util.Log.d("ApiConfig", "URL: ${request.url}")
        android.util.Log.d("ApiConfig", "Method: ${request.method}")
        android.util.Log.d("ApiConfig", "Headers: ${request.headers}")

        try {
            val response = chain.proceed(request)
            android.util.Log.d("ApiConfig", "=== API RESPONSE ===")
            android.util.Log.d("ApiConfig", "Code: ${response.code}")
            android.util.Log.d("ApiConfig", "Message: ${response.message}")
            response
        } catch (e: Exception) {
            android.util.Log.e("ApiConfig", "=== API REQUEST FAILED ===")
            android.util.Log.e("ApiConfig", "Error: ${e.message}", e)
            throw e
        }
    }

    // Optimized client with better timeout values and connection management
    private val client = OkHttpClient.Builder()
        .addInterceptor(customInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)  // Reduced from 60 to prevent hanging
        .readTimeout(30, TimeUnit.SECONDS)    // Reduced from 120 to prevent hanging
        .writeTimeout(30, TimeUnit.SECONDS)   // Reduced from 60 to prevent hanging
        .callTimeout(60, TimeUnit.SECONDS)    // Reduced from 180 to prevent hanging
        .retryOnConnectionFailure(true)       // Keep retrying on connection failures
        .connectionPool(okhttp3.ConnectionPool(5, 10, TimeUnit.MINUTES)) // Increased max idle connections
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}