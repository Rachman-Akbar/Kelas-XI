package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easyshop.data.model.User
import com.komputerkit.easyshop.data.repository.AuthRepository
import com.komputerkit.easyshop.utils.Resource
import com.komputerkit.easyshop.utils.ValidationUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val showToast: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        _isUserLoggedIn.value = authRepository.isUserLoggedIn()
        if (_isUserLoggedIn.value) {
            getCurrentUser()
        }
    }
    
    fun signUp(
        name: String,
        email: String,
        phone: String,
        address: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        // Validasi input
        val nameValidation = ValidationUtil.validateName(name)
        if (!nameValidation.successful) {
            _authState.value = _authState.value.copy(error = nameValidation.errorMessage)
            return
        }
        
        val emailValidation = ValidationUtil.validateEmail(email)
        if (!emailValidation.successful) {
            _authState.value = _authState.value.copy(error = emailValidation.errorMessage)
            return
        }
        
        val phoneValidation = ValidationUtil.validatePhone(phone)
        if (!phoneValidation.successful) {
            _authState.value = _authState.value.copy(error = phoneValidation.errorMessage)
            return
        }
        
        val passwordValidation = ValidationUtil.validatePassword(password)
        if (!passwordValidation.successful) {
            _authState.value = _authState.value.copy(error = passwordValidation.errorMessage)
            return
        }
        
        val confirmPasswordValidation = ValidationUtil.validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.successful) {
            _authState.value = _authState.value.copy(error = confirmPasswordValidation.errorMessage)
            return
        }
        
        // Proses registrasi
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            val user = User(
                name = name,
                email = email,
                phone = phone,
                address = address
            )
            
            when (val result = authRepository.signUp(email, password, user)) {
                is Resource.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null
                    )
                    _isUserLoggedIn.value = true
                    onSuccess()
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit = {}
    ) {
        // Validasi input
        val emailValidation = ValidationUtil.validateEmail(email)
        if (!emailValidation.successful) {
            _authState.value = _authState.value.copy(error = emailValidation.errorMessage)
            return
        }
        
        val passwordValidation = ValidationUtil.validatePassword(password)
        if (!passwordValidation.successful) {
            _authState.value = _authState.value.copy(error = passwordValidation.errorMessage)
            return
        }
        
        // Proses login
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            when (val result = authRepository.signIn(email, password)) {
                is Resource.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        error = null,
                        showToast = "Login berhasil!"
                    )
                    _isUserLoggedIn.value = true
                    getCurrentUser()
                    onSuccess()
                }
                is Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message,
                        showToast = result.message
                    )
                }
                is Resource.Loading -> {
                    _authState.value = _authState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    private fun getCurrentUser() {
        viewModelScope.launch {
            when (val result = authRepository.getCurrentUser()) {
                is Resource.Success -> {
                    _authState.value = _authState.value.copy(user = result.data)
                }
                is Resource.Error -> {
                    // Handle error silently untuk getCurrentUser
                }
                is Resource.Loading -> {
                    // Loading state handled by individual operations
                }
            }
        }
    }
    
    fun signOut() {
        authRepository.signOut()
        _isUserLoggedIn.value = false
        _authState.value = AuthState() // Reset state
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
    
    fun resetSuccessState() {
        _authState.value = _authState.value.copy(isSuccess = false)
    }
    
    fun clearToast() {
        _authState.value = _authState.value.copy(showToast = null)
    }
}