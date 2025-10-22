package com.komputerkit.adminwof.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.komputerkit.adminwof.data.repository.AdminRepository
import com.komputerkit.adminwof.data.repository.AuthRepository

/**
 * ViewModelFactory untuk dependency injection
 * Menggunakan AdminRepository tunggal untuk Product dan Order operations
 */
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> {
                ProductViewModel(adminRepository) as T
            }
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(adminRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
