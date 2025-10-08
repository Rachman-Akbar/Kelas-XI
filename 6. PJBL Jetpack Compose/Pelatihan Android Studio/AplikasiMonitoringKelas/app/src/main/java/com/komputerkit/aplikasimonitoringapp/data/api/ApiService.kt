package com.komputerkit.aplikasimonitoringapp.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<User>>
    
    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<User>>
    
    @GET("users")
    suspend fun getAllUsers(): Response<ApiResponse<List<User>>>
    
    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): Response<ApiResponse<User>>
    
    @POST("logout")
    suspend fun logout(): Response<ApiResponse<String>>
    
    @GET("test")
    suspend fun testConnection(): Response<ApiResponse<String>>
}
