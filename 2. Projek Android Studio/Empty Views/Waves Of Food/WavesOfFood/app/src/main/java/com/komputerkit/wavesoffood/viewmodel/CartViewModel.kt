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
 * Data class untuk cart item dengan product details
 */
data class CartItem(
    val product: ProductModel,
    val quantity: Int
)

/**
 * ViewModel untuk Shopping Cart
 */
class CartViewModel(
    private val repository: EcommerceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems
    
    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> = _totalPrice
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch cart items dengan product details
     */
    fun fetchCartItems() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.getCartItems(userId)
            
            result.onSuccess { cartMap ->
                // Fetch product details untuk setiap item
                val items = mutableListOf<CartItem>()
                var total = 0.0
                
                for ((productId, quantity) in cartMap) {
                    val productResult = repository.fetchProductById(productId)
                    productResult.onSuccess { product ->
                        items.add(CartItem(product, quantity))
                        total += product.price * quantity
                    }
                }
                
                _cartItems.value = items
                _totalPrice.value = total
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load cart"
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
                fetchCartItems() // Refresh cart
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to add to cart"
            }
        }
    }
    
    /**
     * Remove product from cart
     */
    fun removeFromCart(productId: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            val result = repository.removeFromCart(userId, productId)
            
            result.onSuccess {
                fetchCartItems() // Refresh cart
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to remove from cart"
            }
        }
    }
    
    /**
     * Update quantity of product in cart
     */
    fun updateQuantity(productId: String, quantity: Int) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            val result = repository.updateCartQuantity(userId, productId, quantity)
            
            result.onSuccess {
                fetchCartItems() // Refresh cart
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update quantity"
            }
        }
    }
    
    /**
     * Clear entire cart
     */
    fun clearCart() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            repository.clearCart(userId)
        }
    }
}
