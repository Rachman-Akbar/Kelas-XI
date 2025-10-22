package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository

/**
 * ViewModelFactory untuk dependency injection
 * Mengelola pembuatan ViewModel dengan Repository dependencies
 */
class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val ecommerceRepository: EcommerceRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, ecommerceRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(ecommerceRepository) as T
            }
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> {
                ProductDetailViewModel(ecommerceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                CartViewModel(ecommerceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(CheckoutViewModel::class.java) -> {
                CheckoutViewModel(ecommerceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(ecommerceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(OrdersViewModel::class.java) -> {
                OrdersViewModel(ecommerceRepository, authRepository) as T
            }
            modelClass.isAssignableFrom(OrderDetailViewModel::class.java) -> {
                OrderDetailViewModel(ecommerceRepository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(ecommerceRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
