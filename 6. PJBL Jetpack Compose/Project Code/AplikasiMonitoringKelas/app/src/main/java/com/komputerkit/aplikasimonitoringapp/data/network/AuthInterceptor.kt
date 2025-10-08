package com.komputerkit.aplikasimonitoringapp.data.network

import android.content.Context
import com.komputerkit.aplikasimonitoringapp.data.preferences.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Auth Interceptor untuk menambahkan Bearer token ke setiap request
 */
class AuthInterceptor(private val context: Context) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionManager = SessionManager(context)
        val token = sessionManager.getToken()
        
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
        } else {
            chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
        }
        
        return chain.proceed(request)
    }
}
