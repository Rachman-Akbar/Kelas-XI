package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import com.komputerkit.wavesoffood.utils.AuthState
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Authentication
 * Menggunakan Repository pattern dengan Kotlin Coroutines
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val ecommerceRepository: EcommerceRepository
) : ViewModel() {
    
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    
    /**
     * Sign up user baru
     */
    fun signUpWithEmail(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.signUp(email, password, name)
            
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Registration failed"
                )
            }
        }
    }
    
    /**
     * Sign in dengan email dan password
     */
    fun signInWithEmail(email: String, password: String) {
        _authState.value = AuthState.Loading
        
        viewModelScope.launch {
            val result = authRepository.signIn(email, password)
            
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Login failed"
                )
            }
        }
    }
    
    /**
     * Sign out user
     */
    fun signOut() {
        authRepository.signOut()
    }
    
    /**
     * Check if user is logged in
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
    
    /**
     * Reset auth state ke Idle
     */
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
