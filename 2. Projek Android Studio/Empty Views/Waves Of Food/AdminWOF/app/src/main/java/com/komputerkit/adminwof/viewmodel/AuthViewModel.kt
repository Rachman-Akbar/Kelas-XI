package com.komputerkit.adminwof.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.adminwof.data.repository.AuthRepository
import com.komputerkit.adminwof.utils.AuthState
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Authentication
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState
    
    /**
     * Sign in dengan admin validation
     */
    fun signIn(email: String, password: String) {
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
     * Sign out
     */
    fun signOut() {
        authRepository.signOut()
    }
}
