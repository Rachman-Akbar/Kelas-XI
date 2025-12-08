package com.komputerkit.aplikasimonitoringkelas.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.komputerkit.aplikasimonitoringkelas.data.api.ApiConfig
import com.komputerkit.aplikasimonitoringkelas.data.models.LoginRequest
import com.komputerkit.aplikasimonitoringkelas.data.models.LoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthRepository(private val context: Context) {
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_KEY = stringPreferencesKey("user_data")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val KELAS_ID_KEY = stringPreferencesKey("kelas_id")
        private val GURU_ID_KEY = stringPreferencesKey("guru_id")
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val loginRequest = LoginRequest(email, password)
            // Retry mechanism with exponential backoff
            var attempts = 0
            val maxAttempts = 2  // Reduced attempts since timeout is shorter

            var lastException: Exception? = null
            while (attempts < maxAttempts) {
                try {
                    val response = ApiConfig.apiService.login(loginRequest)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.success) {
                            android.util.Log.d("AuthRepository", "=== LOGIN SUCCESS ===")
                            android.util.Log.d("AuthRepository", "User ID: ${loginResponse.data?.user?.id}")
                            android.util.Log.d("AuthRepository", "User Name: ${loginResponse.data?.user?.name}")
                            android.util.Log.d("AuthRepository", "User Role: ${loginResponse.data?.user?.role}")
                            android.util.Log.d("AuthRepository", "Kelas ID from API: ${loginResponse.data?.user?.kelas_id}")
                            android.util.Log.d("AuthRepository", "Guru ID from API: ${loginResponse.data?.user?.guru_id}")

                            // Save token and user info to DataStore
                            saveToken(loginResponse.data?.token ?: "")
                            saveUserInfo(
                                id = loginResponse.data?.user?.id ?: 0,
                                name = loginResponse.data?.user?.name ?: "",
                                role = loginResponse.data?.user?.role ?: "",
                                kelasId = loginResponse.data?.user?.kelas_id,
                                guruId = loginResponse.data?.user?.guru_id
                            )
                            return Result.success(loginResponse)
                        } else {
                            return Result.failure(Exception(loginResponse?.message ?: "Login failed"))
                        }
                    } else {
                        return Result.failure(Exception(response.message()))
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    attempts++
                    lastException = e
                    if (attempts >= maxAttempts) {
                        break
                    }
                    kotlinx.coroutines.delay(2000L) // Wait 2 seconds between retries
                } catch (e: java.net.UnknownHostException) {
                    // Don't retry on unknown host - likely configuration issue
                    return Result.failure(Exception("Tidak dapat menemukan server. Pastikan server berjalan dan IP Anda benar."))
                } catch (e: java.net.ConnectException) {
                    attempts++
                    lastException = e
                    if (attempts >= maxAttempts) {
                        break
                    }
                    kotlinx.coroutines.delay(1000L) // Wait 1 second between retries
                } catch (e: java.io.IOException) {
                    attempts++
                    lastException = e
                    if (attempts >= maxAttempts) {
                        break
                    }
                    kotlinx.coroutines.delay(1500L) // Wait 1.5 seconds between retries
                } catch (e: Exception) {
                    // For other exceptions, don't retry - return immediately
                    return Result.failure(e)
                }
            }

            // If all attempts failed, return the last exception
            Result.failure(lastException ?: Exception("Login failed after $maxAttempts attempts"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val token = getToken()
            if (token.isNotEmpty()) {
                ApiConfig.apiService.logout("Bearer $token")
            }
            clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isAuthenticated(): Boolean {
        return getToken().isNotEmpty()
    }

    suspend fun getToken(): String {
        val preferences = context.dataStore.data.first()
        return preferences[TOKEN_KEY] ?: ""
    }

    private suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    private suspend fun saveUserInfo(id: Int, name: String, role: String, kelasId: Int? = null, guruId: Int? = null) {
        android.util.Log.d("AuthRepository", "=== SAVING USER INFO TO DATASTORE ===")
        android.util.Log.d("AuthRepository", "ID: $id")
        android.util.Log.d("AuthRepository", "Name: $name")
        android.util.Log.d("AuthRepository", "Role: $role")
        android.util.Log.d("AuthRepository", "Kelas ID to save: $kelasId")
        android.util.Log.d("AuthRepository", "Guru ID to save: $guruId")
        
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id.toString()
            preferences[USER_NAME_KEY] = name
            preferences[USER_ROLE_KEY] = role
            if (kelasId != null) {
                preferences[KELAS_ID_KEY] = kelasId.toString()
                android.util.Log.d("AuthRepository", "Kelas ID SAVED: ${kelasId.toString()}")
            } else {
                android.util.Log.w("AuthRepository", "Kelas ID is NULL - not saving!")
            }
            if (guruId != null) {
                preferences[GURU_ID_KEY] = guruId.toString()
                android.util.Log.d("AuthRepository", "Guru ID SAVED: ${guruId.toString()}")
            } else {
                android.util.Log.w("AuthRepository", "Guru ID is NULL - not saving!")
            }
        }
        
        android.util.Log.d("AuthRepository", "=== USER INFO SAVED ===")
    }

    suspend fun getUserId(): Int {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]?.toIntOrNull() ?: 0
    }

    suspend fun getUserName(): String {
        val preferences = context.dataStore.data.first()
        return preferences[USER_NAME_KEY] ?: ""
    }

    suspend fun getUserRole(): String {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ROLE_KEY] ?: ""
    }

    suspend fun getKelasId(): Int? {
        val preferences = context.dataStore.data.first()
        val kelasIdString = preferences[KELAS_ID_KEY]
        val kelasId = kelasIdString?.toIntOrNull()
        
        android.util.Log.d("AuthRepository", "=== GET KELAS ID ===")
        android.util.Log.d("AuthRepository", "Kelas ID String from DataStore: $kelasIdString")
        android.util.Log.d("AuthRepository", "Kelas ID Int: $kelasId")
        
        return kelasId
    }

    suspend fun getGuruId(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[GURU_ID_KEY]?.toIntOrNull()
    }

    private suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getUserInfo(): Result<LoginResponse> {
        return try {
            val token = getToken()
            if (token.isNotEmpty()) {
                val response = ApiConfig.apiService.getUserInfo("Bearer $token")
                if (response.isSuccessful) {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        Result.success(userInfo)
                    } else {
                        Result.failure(Exception("User info not available"))
                    }
                } else {
                    Result.failure(Exception(response.message()))
                }
            } else {
                Result.failure(Exception("No token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}