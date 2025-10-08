package com.komputerkit.firebaseauthdemoapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: com.google.firebase.auth.FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState
    
    init {
        checkAuthStatus()
    }
    
    private fun checkAuthStatus() {
        val currentUser = authRepository.currentUser
        _authState.value = _authState.value.copy(
            isAuthenticated = currentUser != null,
            user = currentUser
        )
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            authRepository.login(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        user = null,
                        error = exception.message
                    )
                }
        }
    }
    
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            authRepository.register(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        user = user,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        user = null,
                        error = exception.message
                    )
                }
        }
    }
    
    fun logout() {
        authRepository.logout()
        _authState.value = _authState.value.copy(
            isAuthenticated = false,
            user = null,
            error = null
        )
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}