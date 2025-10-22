package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.komputerkit.wavesoffood.data.model.OrderModel
import com.komputerkit.wavesoffood.data.model.UserModel
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import com.komputerkit.wavesoffood.utils.Constants
import com.komputerkit.wavesoffood.utils.PaymentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel untuk Checkout & Payment
 */
class CheckoutViewModel(
    private val repository: EcommerceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user
    
    private val _paymentState = MutableLiveData<PaymentState>(PaymentState.Idle)
    val paymentState: LiveData<PaymentState> = _paymentState
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch user data
     */
    fun fetchUserData() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.fetchUser(userId)
            
            result.onSuccess { userData ->
                _user.value = userData
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load user data"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Calculate totals (subtotal, tax, total)
     */
    fun calculateTotals(subtotal: Double): Triple<Double, Double, Double> {
        val tax = subtotal * Constants.TAX_RATE
        val total = subtotal + tax
        return Triple(subtotal, tax, total)
    }
    
    /**
     * Process payment dan create order
     */
    fun processPayment(subtotal: Double, address: String) {
        val userId = authRepository.getCurrentUserId() ?: return
        
        // Validate address
        if (address.isBlank()) {
            _paymentState.value = PaymentState.Error("Please enter shipping address")
            return
        }
        
        _paymentState.value = PaymentState.Processing
        
        viewModelScope.launch {
            try {
                // Simulate payment processing
                delay(Constants.PAYMENT_DELAY_MS)
                
                // Calculate totals
                val (_, tax, total) = calculateTotals(subtotal)
                
                // Get cart items
                val cartResult = repository.getCartItems(userId)
                val cartItems = cartResult.getOrThrow()
                
                if (cartItems.isEmpty()) {
                    _paymentState.value = PaymentState.Error("Cart is empty")
                    return@launch
                }
                
                // Create order
                val orderId = UUID.randomUUID().toString()
                val order = OrderModel(
                    id = orderId,
                    userID = userId,
                    date = Timestamp.now(),
                    items = cartItems,
                    status = Constants.ORDER_STATUS_PENDING,
                    address = address
                )
                
                val orderResult = repository.createOrder(order)
                
                orderResult.onSuccess {
                    // Clear cart after successful order
                    repository.clearCart(userId)
                    _paymentState.value = PaymentState.Success(orderId)
                }.onFailure { exception ->
                    _paymentState.value = PaymentState.Error(
                        exception.message ?: "Failed to create order"
                    )
                }
                
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(
                    e.message ?: "Payment failed"
                )
            }
        }
    }
    
    /**
     * Reset payment state
     */
    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}
