package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.model.OrderModel
import com.komputerkit.wavesoffood.data.repository.AuthRepository
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Orders History
 */
class OrdersViewModel(
    private val repository: EcommerceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> = _orders
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch user's orders
     */
    fun fetchOrders() {
        val userId = authRepository.getCurrentUserId() ?: return
        
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchUserOrders(userId)
            
            result.onSuccess { orderList ->
                _orders.value = orderList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load orders"
                _isLoading.value = false
            }
        }
    }
}
