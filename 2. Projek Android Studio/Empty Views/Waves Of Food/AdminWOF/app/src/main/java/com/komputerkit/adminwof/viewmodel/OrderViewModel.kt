package com.komputerkit.adminwof.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.adminwof.data.model.OrderModel
import com.komputerkit.adminwof.data.model.ProductModel
import com.komputerkit.adminwof.data.model.UserModel
import com.komputerkit.adminwof.data.repository.AdminRepository
import com.komputerkit.adminwof.utils.UiState
import kotlinx.coroutines.launch

/**
 * Data class untuk Order Item dengan detail produk
 */
data class OrderItemDetail(
    val product: ProductModel,
    val quantity: Int
)

/**
 * ViewModel untuk Order Management
 * Menggunakan AdminRepository untuk semua operasi orders
 */
class OrderViewModel(
    private val repository: AdminRepository
) : ViewModel() {
    
    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> = _orders
    
    private val _order = MutableLiveData<OrderModel?>()
    val order: LiveData<OrderModel?> = _order
    
    private val _user = MutableLiveData<UserModel?>()
    val user: LiveData<UserModel?> = _user
    
    private val _orderItems = MutableLiveData<List<OrderItemDetail>>()
    val orderItems: LiveData<List<OrderItemDetail>> = _orderItems
    
    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> = _totalPrice
    
    private val _uiState = MutableLiveData<UiState<String>>(UiState.Idle)
    val uiState: LiveData<UiState<String>> = _uiState
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch all orders menggunakan AdminRepository
     */
    fun fetchAllOrders() {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.fetchAllOrders()
            
            result.onSuccess { orderList ->
                _orders.value = orderList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load orders"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch order detail dengan user dan product info
     */
    fun fetchOrderDetail(orderId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val orderResult = repository.fetchOrderById(orderId)
            
            orderResult.onSuccess { orderData ->
                _order.value = orderData
                
                // Fetch user info
                val userResult = repository.fetchUserById(orderData.userID)
                userResult.onSuccess { userData ->
                    _user.value = userData
                }
                
                // Fetch product details for all items
                fetchProductDetails(orderData.items)
                
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load order"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch product details dan calculate total
     */
    private fun fetchProductDetails(items: Map<String, Int>) {
        viewModelScope.launch {
            val itemDetails = mutableListOf<OrderItemDetail>()
            var total = 0.0
            
            items.forEach { (productId, quantity) ->
                val result = repository.fetchProductById(productId)
                result.onSuccess { product ->
                    itemDetails.add(OrderItemDetail(product, quantity))
                    total += product.price * quantity
                }
            }
            
            _orderItems.value = itemDetails
            _totalPrice.value = total
        }
    }
    
    /**
     * Update order status dengan validation
     */
    fun updateOrderStatus(orderId: String, newStatus: String) {
        // Validate status
        if (!repository.validateOrderStatus(newStatus)) {
            _uiState.value = UiState.Error("Invalid order status")
            return
        }
        
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            val result = repository.updateOrderStatus(orderId, newStatus)
            
            result.onSuccess {
                _uiState.value = UiState.Success("Order status updated to $newStatus")
                // Refresh order detail
                fetchOrderDetail(orderId)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Failed to update order status"
                )
            }
        }
    }
    
    /**
     * Reset UI state
     */
    fun resetUiState() {
        _uiState.value = UiState.Idle
    }
}
