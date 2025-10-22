package com.komputerkit.wavesoffood.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.wavesoffood.data.model.OrderModel
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import kotlinx.coroutines.launch

/**
 * Data class untuk order detail item dengan product details
 */
data class OrderDetailItem(
    val product: ProductModel,
    val quantity: Int,
    val subtotal: Double
)

/**
 * ViewModel untuk Order Detail
 */
class OrderDetailViewModel(
    private val repository: EcommerceRepository
) : ViewModel() {
    
    private val _orderDetail = MutableLiveData<OrderModel?>()
    val orderDetail: LiveData<OrderModel?> = _orderDetail
    
    private val _orderItems = MutableLiveData<List<OrderDetailItem>>()
    val orderItems: LiveData<List<OrderDetailItem>> = _orderItems
    
    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> = _totalPrice
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch order detail dengan product breakdown
     */
    fun fetchOrderDetail(orderId: String) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch {
            val result = repository.fetchOrderById(orderId)
            
            result.onSuccess { order ->
                _orderDetail.value = order
                
                // Fetch product details untuk setiap item
                fetchProductDetails(order.items)
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load order"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch product details untuk semua items dalam order
     */
    private fun fetchProductDetails(items: Map<String, Int>) {
        viewModelScope.launch {
            val detailItems = mutableListOf<OrderDetailItem>()
            var total = 0.0
            
            for ((productId, quantity) in items) {
                val productResult = repository.fetchProductById(productId)
                
                productResult.onSuccess { product ->
                    val subtotal = product.price * quantity
                    detailItems.add(
                        OrderDetailItem(
                            product = product,
                            quantity = quantity,
                            subtotal = subtotal
                        )
                    )
                    total += subtotal
                }
            }
            
            _orderItems.value = detailItems
            _totalPrice.value = total
            _isLoading.value = false
        }
    }
}
