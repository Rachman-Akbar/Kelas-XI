package com.komputerkit.aplikasimonitoringapp.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringapp.data.model.LoginRequest
import com.komputerkit.aplikasimonitoringapp.data.network.ApiClient
import com.komputerkit.aplikasimonitoringapp.data.network.ApiService
import com.komputerkit.aplikasimonitoringapp.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI State untuk Login
 */
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val role: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

/**
 * Login ViewModel
 */
class LoginViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    /**
     * Fungsi login
     */
    fun login(email: String, password: String, context: Context) {
        // Validasi input
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email dan Password harus diisi")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = LoginUiState.Error("Format email tidak valid")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = LoginUiState.Loading
                
                // Buat API service tanpa auth
                val apiService: ApiService = ApiClient.createService(ApiService::class.java)
                
                // Call API login
                val response = apiService.login(LoginRequest(email, password))
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    
                    if (loginResponse.success) {
                        // Validasi token tidak null
                        val token = loginResponse.data.accessToken
                        if (token.isNullOrEmpty()) {
                            _uiState.value = LoginUiState.Error("Token tidak valid dari server")
                            return@launch
                        }
                        
                        // Save token dan user data
                        val sessionManager = SessionManager(context)
                        sessionManager.saveToken(token)
                        sessionManager.saveUserData(loginResponse.data.user)
                        
                        // Success dengan role
                        _uiState.value = LoginUiState.Success(loginResponse.data.user.role)
                    } else {
                        _uiState.value = LoginUiState.Error(loginResponse.message)
                    }
                } else {
                    // Error dari server
                    val errorBody = response.errorBody()?.string()
                    _uiState.value = LoginUiState.Error(
                        errorBody ?: "Login gagal: ${response.code()}"
                    )
                }
            } catch (e: java.net.UnknownHostException) {
                _uiState.value = LoginUiState.Error(
                    "Tidak dapat terhubung ke server. Pastikan:\n" +
                    "1. Laravel server running (php artisan serve)\n" +
                    "2. IP address di ApiClient.kt sudah benar"
                )
            } catch (e: java.net.ConnectException) {
                _uiState.value = LoginUiState.Error(
                    "Connection refused. Pastikan Laravel server running di http://127.0.0.1:8000"
                )
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    "Error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Reset state
     */
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
