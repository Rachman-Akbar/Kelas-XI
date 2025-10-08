package com.komputerkit.aplikasimonitoringapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Base URL untuk Android Emulator
    // Untuk Physical Device, ganti dengan IP komputer Anda (misal: "http://192.168.1.XXX:8000/api/")
    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    // Alternative URLs:
    // Emulator: http://10.0.2.2:8000/api/
    // Physical Device: http://192.168.1.XXX:8000/api/ (ganti XXX dengan IP komputer)
    // Localhost: http://127.0.0.1:8000/api/
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    // Helper function untuk mendapatkan base URL yang sedang digunakan
    fun getBaseUrl(): String = BASE_URL
    
    // Helper function untuk mengubah base URL (jika diperlukan)
    fun createServiceWithCustomUrl(baseUrl: String): ApiService {
        val customRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return customRetrofit.create(ApiService::class.java)
    }
}
