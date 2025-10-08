package com.komputerkit.aplikasimonitoringapp.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * API Client Singleton
 * 
 * PENTING: Ganti BASE_URL sesuai dengan device Anda:
 * - Emulator: http://10.0.2.2:8000/api/
 * - Physical Device: http://192.168.X.X:8000/api/ (IP laptop Anda)
 */
object ApiClient {
    
    // ⚠️ GANTI IP ADDRESS SESUAI KEBUTUHAN ⚠️
    // Emulator gunakan: 10.0.2.2
    // Physical device gunakan: IP laptop (cek dengan ipconfig)
    // Updated untuk physical device: IP laptop adalah 192.168.1.4
    private const val BASE_URL = "http://192.168.1.4:8000/api/"
    
    private const val CONNECT_TIMEOUT = 30L // seconds
    private const val READ_TIMEOUT = 30L // seconds
    private const val WRITE_TIMEOUT = 30L // seconds
    
    /**
     * Logging Interceptor untuk debug
     */
    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    /**
     * OkHttp Client tanpa authentication (untuk login/register)
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(getLoggingInterceptor())
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    /**
     * Retrofit instance tanpa auth
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    /**
     * Create service tanpa authentication
     * Gunakan untuk endpoint login/register
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
    
    /**
     * Create service dengan authentication
     * Gunakan untuk endpoint yang membutuhkan token
     */
    fun <T> createAuthService(serviceClass: Class<T>, context: Context): T {
        val authClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(getLoggingInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
        
        val authRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return authRetrofit.create(serviceClass)
    }
}
