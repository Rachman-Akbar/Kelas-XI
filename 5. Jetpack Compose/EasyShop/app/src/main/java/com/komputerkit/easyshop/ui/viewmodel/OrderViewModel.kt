package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easyshop.data.model.Order
import com.komputerkit.easyshop.data.model.OrderItem
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.model.ShippingAddress
import com.komputerkit.easyshop.data.repository.OrderRepository
import com.komputerkit.easyshop.data.repository.ProductRepository
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

data class OrderDetailState(
    val order: Order? = null,
    val orderProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _ordersState = MutableStateFlow(OrdersState())
    val ordersState: StateFlow<OrdersState> = _ordersState.asStateFlow()
    
    private val _orderDetailState = MutableStateFlow(OrderDetailState())
    val orderDetailState: StateFlow<OrderDetailState> = _orderDetailState.asStateFlow()
    
    init {
        loadOrders()
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _ordersState.value = _ordersState.value.copy(isLoading = true, error = null)
            
            when (val result = orderRepository.getUserOrders()) {
                is Resource.Success -> {
                    _ordersState.value = _ordersState.value.copy(
                        orders = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _ordersState.value = _ordersState.value.copy(
                        orders = emptyList(),
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _ordersState.value = _ordersState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _orderDetailState.value = _orderDetailState.value.copy(isLoading = true, error = null)
            
            when (val orderResult = orderRepository.getOrderById(orderId)) {
                is Resource.Success -> {
                    val order = orderResult.data
                    if (order != null) {
                        // Load product details for each order item
                        loadOrderProducts(order.items)
                        
                        _orderDetailState.value = _orderDetailState.value.copy(
                            order = order,
                            isLoading = false,
                            error = null
                        )
                    } else {
                        _orderDetailState.value = _orderDetailState.value.copy(
                            order = null,
                            isLoading = false,
                            error = "Pesanan tidak ditemukan"
                        )
                    }
                }
                is Resource.Error -> {
                    _orderDetailState.value = _orderDetailState.value.copy(
                        order = null,
                        isLoading = false,
                        error = orderResult.message
                    )
                }
                is Resource.Loading -> {
                    _orderDetailState.value = _orderDetailState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    private suspend fun loadOrderProducts(orderItems: List<OrderItem>) {
        val products = mutableListOf<Product>()
        
        orderItems.forEach { orderItem ->
            when (val productResult = productRepository.getProductById(orderItem.productId)) {
                is Resource.Success -> {
                    productResult.data?.let { product ->
                        products.add(product)
                    }
                }
                else -> {
                    // Create placeholder product if not found
                    products.add(
                        Product(
                            id = orderItem.productId,
                            name = orderItem.productName,
                            price = orderItem.productPrice,
                            imageUrl = orderItem.productImageUrl
                        )
                    )
                }
            }
        }
        
        _orderDetailState.value = _orderDetailState.value.copy(orderProducts = products)
    }
    
    fun createOrder(
        items: List<OrderItem>,
        shippingAddress: ShippingAddress,
        subtotal: Double,
        tax: Double,
        shippingCost: Double,
        total: Double
    ) {
        viewModelScope.launch {
            _ordersState.value = _ordersState.value.copy(isLoading = true, error = null)
            
            when (val result = orderRepository.createOrder(
                items, shippingAddress, subtotal, tax, shippingCost, total
            )) {
                is Resource.Success -> {
                    _ordersState.value = _ordersState.value.copy(
                        isLoading = false,
                        successMessage = result.data,
                        error = null
                    )
                    loadOrders() // Refresh orders list
                }
                is Resource.Error -> {
                    _ordersState.value = _ordersState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _ordersState.value = _ordersState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            when (val result = orderRepository.cancelOrder(orderId)) {
                is Resource.Success -> {
                    _ordersState.value = _ordersState.value.copy(
                        successMessage = result.data
                    )
                    loadOrders() // Refresh orders list
                    // Also refresh detail if we're viewing this order
                    if (_orderDetailState.value.order?.id == orderId) {
                        loadOrderDetail(orderId)
                    }
                }
                is Resource.Error -> {
                    _ordersState.value = _ordersState.value.copy(
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    // Handle loading if needed
                }
            }
        }
    }
    
    fun clearMessages() {
        _ordersState.value = _ordersState.value.copy(
            error = null,
            successMessage = null
        )
    }
    
    fun clearError() {
        _ordersState.value = _ordersState.value.copy(error = null)
        _orderDetailState.value = _orderDetailState.value.copy(error = null)
    }
    
    fun clearSuccessMessage() {
        _ordersState.value = _ordersState.value.copy(successMessage = null)
    }
}