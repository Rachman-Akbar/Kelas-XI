package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easyshop.data.model.CartItem
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.repository.CartRepository
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val cartItemCount: Int = 0,
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shippingCost: Double = 0.0,
    val total: Double = 0.0
)

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _cartState = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()
    
    init {
        loadCartItems()
    }
    
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            _cartState.value = _cartState.value.copy(isLoading = true, error = null)
            
            when (val result = cartRepository.addToCart(product, quantity)) {
                is Resource.Success -> {
                    _cartState.value = _cartState.value.copy(
                        isLoading = false,
                        successMessage = result.data,
                        error = null
                    )
                    loadCartItems() // Refresh cart after adding
                }
                is Resource.Error -> {
                    _cartState.value = _cartState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _cartState.value = _cartState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun loadCartItems() {
        viewModelScope.launch {
            _cartState.value = _cartState.value.copy(isLoading = true, error = null)
            
            when (val result = cartRepository.getCartItems()) {
                is Resource.Success -> {
                    val cartItems = result.data ?: emptyList()
                    val subtotal = cartItems.sumOf { it.totalPrice }
                    val itemCount = cartItems.sumOf { it.quantity }
                    val tax = subtotal * 0.11 // PPN 11%
                    val shippingCost = if (subtotal > 0) 15000.0 else 0.0 // Ongkir 15k
                    val total = subtotal + tax + shippingCost
                    
                    _cartState.value = _cartState.value.copy(
                        cartItems = cartItems,
                        subtotal = subtotal,
                        cartItemCount = itemCount,
                        tax = tax,
                        shippingCost = shippingCost,
                        total = total,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _cartState.value = _cartState.value.copy(
                        cartItems = emptyList(),
                        subtotal = 0.0,
                        cartItemCount = 0,
                        tax = 0.0,
                        shippingCost = 0.0,
                        total = 0.0,
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _cartState.value = _cartState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId)
            return
        }
        
        viewModelScope.launch {
            when (val result = cartRepository.updateCartItemQuantity(productId, newQuantity)) {
                is Resource.Success -> {
                    _cartState.value = _cartState.value.copy(
                        successMessage = result.data,
                        error = null
                    )
                    loadCartItems() // Refresh cart after updating
                }
                is Resource.Error -> {
                    _cartState.value = _cartState.value.copy(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    // Loading handled by individual item update
                }
            }
        }
    }
    
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            when (val result = cartRepository.removeFromCart(productId)) {
                is Resource.Success -> {
                    _cartState.value = _cartState.value.copy(
                        successMessage = result.data,
                        error = null
                    )
                    loadCartItems() // Refresh cart after removing
                }
                is Resource.Error -> {
                    _cartState.value = _cartState.value.copy(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    // Loading handled by cart refresh
                }
            }
        }
    }
    
    fun clearCart() {
        viewModelScope.launch {
            _cartState.value = _cartState.value.copy(isLoading = true, error = null)
            
            when (val result = cartRepository.clearCart()) {
                is Resource.Success -> {
                    _cartState.value = _cartState.value.copy(
                        cartItems = emptyList(),
                        subtotal = 0.0,
                        cartItemCount = 0,
                        isLoading = false,
                        successMessage = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _cartState.value = _cartState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _cartState.value = _cartState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun getCartItemCount() {
        viewModelScope.launch {
            when (val result = cartRepository.getCartItemCount()) {
                is Resource.Success -> {
                    _cartState.value = _cartState.value.copy(
                        cartItemCount = result.data ?: 0
                    )
                }
                is Resource.Error -> {
                    // Silently handle error for count
                }
                is Resource.Loading -> {
                    // Loading handled by main cart loading
                }
            }
        }
    }
    
    fun clearMessages() {
        _cartState.value = _cartState.value.copy(
            error = null,
            successMessage = null
        )
    }
    
    fun clearError() {
        _cartState.value = _cartState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _cartState.value = _cartState.value.copy(successMessage = null)
    }
}