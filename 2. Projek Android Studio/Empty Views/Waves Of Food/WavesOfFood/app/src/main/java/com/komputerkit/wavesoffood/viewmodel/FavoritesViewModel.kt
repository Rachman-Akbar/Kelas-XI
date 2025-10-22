package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Favorites Management
 */
class FavoritesViewModel(
    private val repository: EcommerceRepository
) : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()
    
    private val _favoriteProducts = MutableLiveData<List<ProductModel>>()
    val favoriteProducts: LiveData<List<ProductModel>> = _favoriteProducts
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _isFavorite = MutableLiveData<Boolean>(false)
    val isFavorite: LiveData<Boolean> = _isFavorite
    
    /**
     * Fetch all favorite products untuk current user
     */
    fun fetchFavoriteProducts() {
        val userId = auth.currentUser?.uid ?: return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getFavoriteProducts(userId)
            
            result.onSuccess { products ->
                _favoriteProducts.value = products
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load favorites"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle favorite status untuk product
     */
    fun toggleFavorite(productId: String, currentIsFavorite: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            val result = repository.toggleFavorite(userId, productId, !currentIsFavorite)
            
            result.onSuccess {
                _isFavorite.value = !currentIsFavorite
                // Refresh favorites list
                fetchFavoriteProducts()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update favorite"
            }
        }
    }
    
    /**
     * Check apakah product adalah favorite
     */
    fun checkIsFavorite(productId: String) {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            val result = repository.isFavorite(userId, productId)
            
            result.onSuccess { isFav ->
                _isFavorite.value = isFav
            }.onFailure {
                _isFavorite.value = false
            }
        }
    }
    
    /**
     * Remove from favorites
     */
    fun removeFromFavorites(productId: String) {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            val result = repository.toggleFavorite(userId, productId, false)
            
            result.onSuccess {
                // Refresh favorites list
                fetchFavoriteProducts()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to remove favorite"
            }
        }
    }
}
