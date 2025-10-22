package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.model.UserModel
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Profile Management
 */
class ProfileViewModel(
    private val repository: EcommerceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user
    
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch user profile
     */
    fun fetchUserProfile() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchUser(userId)
            
            result.onSuccess { userData ->
                _user.value = userData
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load profile"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update user address
     */
    fun updateAddress(newAddress: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        if (newAddress.isBlank()) {
            _error.value = "Address cannot be empty"
            return
        }
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.updateUserAddress(userId, newAddress)
            
            result.onSuccess {
                _updateSuccess.value = true
                _isLoading.value = false
                // Refresh user data
                fetchUserProfile()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update address"
                _updateSuccess.value = false
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Sign out user
     */
    fun signOut() {
        authRepository.signOut()
    }
}
