package com.komputerkit.mvvm_demo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.mvvm_demo.model.UserData
import com.komputerkit.mvvm_demo.model.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel class that manages UI-related data for the Home screen
 */
class HomeViewModel : ViewModel() {
    
    private val userRepository = UserRepository()
    
    // Private mutable LiveData for user data
    private val _userData = MutableLiveData<UserData?>()
    // Public read-only LiveData for user data
    val userData: LiveData<UserData?> = _userData
    
    // Private mutable LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    // Public read-only LiveData for loading state
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        // Initialize loading state as false
        _isLoading.value = false
    }
    
    /**
     * Function to fetch user data from repository
     */
    fun getUserData() {
        viewModelScope.launch {
            try {
                // Set loading state to true before starting the operation
                _isLoading.value = true
                
                // Fetch user data from repository
                val result = userRepository.fetchUserData()
                
                // Update user data with the result
                _userData.value = result
                
            } catch (e: Exception) {
                // In case of error, you can handle it here
                _userData.value = null
            } finally {
                // Set loading state to false after operation completes
                _isLoading.value = false
            }
        }
    }
}