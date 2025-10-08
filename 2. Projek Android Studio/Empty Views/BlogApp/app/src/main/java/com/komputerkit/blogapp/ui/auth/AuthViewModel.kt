package com.komputerkit.blogapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.blogapp.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val authService = AuthService()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authService.loginUser(email, password)
            
            if (result.isSuccess) {
                _authState.value = AuthState.Success(result.getOrNull())
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Login gagal"
                )
            }
        }
    }
    
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authService.registerUser(email, password)
            
            if (result.isSuccess) {
                _authState.value = AuthState.Success(result.getOrNull())
            } else {
                _authState.value = AuthState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Registrasi gagal"
                )
            }
        }
    }
    
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val user: Any?) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}