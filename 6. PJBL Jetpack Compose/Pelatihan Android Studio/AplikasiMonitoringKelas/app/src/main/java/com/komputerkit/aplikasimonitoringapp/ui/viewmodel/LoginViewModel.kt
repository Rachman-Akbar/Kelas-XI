package com.komputerkit.aplikasimonitoringapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringapp.data.api.User
import com.komputerkit.aplikasimonitoringapp.data.repository.AuthRepository
import com.komputerkit.aplikasimonitoringapp.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val user: User? = null
)

class LoginViewModel : ViewModel() {
    
    private val repository = AuthRepository()
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            
            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        user = result.data
                    )
                }
                is Result.Error -> {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = LoginUiState(isLoading = true)
                }
            }
        }
    }
    
    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            
            when (val result = repository.testConnection()) {
                is Result.Success -> {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = true,
                        errorMessage = "Koneksi berhasil: ${result.data}"
                    )
                }
                is Result.Error -> {
                    _uiState.value = LoginUiState(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = LoginUiState(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
