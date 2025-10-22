package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Product Detail
 */
class ProductDetailViewModel(
    private val repository: EcommerceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _product = MutableLiveData<ProductModel?>()
    val product: LiveData<ProductModel?> = _product
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _addToCartSuccess = MutableLiveData<Boolean>()
    val addToCartSuccess: LiveData<Boolean> = _addToCartSuccess
    
    /**
     * Fetch product by ID
     */
    fun fetchProduct(productId: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchProductById(productId)
            
            result.onSuccess { fetchedProduct ->
                _product.value = fetchedProduct
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load product"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Add product to cart
     */
    fun addToCart(productId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            val result = repository.addToCart(userId, productId)
            
            result.onSuccess {
                _addToCartSuccess.value = true
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to add to cart"
                _addToCartSuccess.value = false
            }
        }
    }
}
