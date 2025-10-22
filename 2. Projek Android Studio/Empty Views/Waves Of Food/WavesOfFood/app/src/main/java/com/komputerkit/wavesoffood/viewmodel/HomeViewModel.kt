package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Home Screen (Product Listing)
 * Menggunakan Repository pattern dengan Kotlin Coroutines
 */
class HomeViewModel(
    private val repository: EcommerceRepository
) : ViewModel() {
    
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch all products
     */
    fun fetchProducts() {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchAllProducts()
            
            result.onSuccess { productList ->
                _products.value = productList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load products"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch products by category
     */
    fun fetchProductsByCategory(category: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchProductsByCategory(category)
            
            result.onSuccess { productList ->
                _products.value = productList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load products"
                _isLoading.value = false
            }
        }
    }
}
