package com.komputerkit.aplikasimonitoringapp.data.repository

import com.komputerkit.aplikasimonitoringapp.data.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class AuthRepository {
    
    private val apiService = RetrofitClient.apiService
    
    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        Result.Success(body.data)
                    } else {
                        Result.Error(body?.message ?: "Login gagal")
                    }
                } else {
                    val errorMessage = parseErrorResponse(response)
                    Result.Error(errorMessage)
                }
            } catch (e: Exception) {
                Result.Error("Kesalahan koneksi: ${e.message}")
            }
        }
    }
    
    suspend fun register(
        nama: String,
        email: String,
        password: String,
        role: String
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(nama, email, password, role)
                val response = apiService.register(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        Result.Success(body.data)
                    } else {
                        Result.Error(body?.message ?: "Registrasi gagal")
                    }
                } else {
                    val errorMessage = parseErrorResponse(response)
                    Result.Error(errorMessage)
                }
            } catch (e: Exception) {
                Result.Error("Kesalahan koneksi: ${e.message}")
            }
        }
    }
    
    suspend fun getAllUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllUsers()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        Result.Success(body.data)
                    } else {
                        Result.Error(body?.message ?: "Gagal mengambil data user")
                    }
                } else {
                    val errorMessage = parseErrorResponse(response)
                    Result.Error(errorMessage)
                }
            } catch (e: Exception) {
                Result.Error("Kesalahan koneksi: ${e.message}")
            }
        }
    }
    
    suspend fun testConnection(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.testConnection()
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        Result.Success(body.message)
                    } else {
                        Result.Error(body?.message ?: "Test koneksi gagal")
                    }
                } else {
                    Result.Error("HTTP Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Result.Error("Kesalahan koneksi: ${e.message}")
            }
        }
    }
    
    private fun <T> parseErrorResponse(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            errorBody ?: "Error ${response.code()}: ${response.message()}"
        } catch (e: Exception) {
            "Error ${response.code()}: ${response.message()}"
        }
    }
}
