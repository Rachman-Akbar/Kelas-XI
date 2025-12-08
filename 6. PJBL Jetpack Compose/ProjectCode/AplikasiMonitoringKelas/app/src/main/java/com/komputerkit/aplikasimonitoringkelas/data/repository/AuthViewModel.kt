package com.komputerkit.aplikasimonitoringkelas.data.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _isAuthenticated.value = authRepository.isAuthenticated()
            println("DEBUG AuthViewModel - isAuthenticated: ${_isAuthenticated.value}")
            
            if (_isAuthenticated.value) {
                val result = authRepository.getUserInfo()
                result.fold(
                    onSuccess = { userInfo ->
                        println("DEBUG AuthViewModel - Success getting user info")
                        println("DEBUG AuthViewModel - userInfo.data: ${userInfo.data}")
                        println("DEBUG AuthViewModel - user: ${userInfo.data?.user}")
                        
                        val role = userInfo.data?.user?.role
                        val name = userInfo.data?.user?.name
                        
                        _userRole.value = role
                        _userName.value = name
                        
                        println("DEBUG AuthViewModel - Role set to: $role")
                        println("DEBUG AuthViewModel - Name set to: $name")
                    },
                    onFailure = { exception ->
                        _isAuthenticated.value = false
                        _userRole.value = null
                        _userName.value = null
                        println("DEBUG AuthViewModel - Failed to get user info: ${exception.message}")
                        exception.printStackTrace()
                    }
                )
            } else {
                println("DEBUG AuthViewModel - User not authenticated")
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isAuthenticated.value = false
            _userRole.value = null
            _userName.value = null
        }
    }

    fun forceCheckAuth() {
        checkAuthenticationStatus()
    }
}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val repository = AuthRepository(context)
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}