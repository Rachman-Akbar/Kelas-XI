package com.example.kishaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kishaapp.data.model.Cart
import com.example.kishaapp.data.model.MarketplaceCartItem
import com.example.kishaapp.data.model.Order
import com.example.kishaapp.data.model.Product
import com.example.kishaapp.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionUiState(
    val isCartLoading: Boolean = false,
    val isCheckoutLoading: Boolean = false,
    val isOrdersLoading: Boolean = false,
    val cart: Cart? = null,
    val cartItems: List<MarketplaceCartItem> = emptyList(),
    val cartSubtotal: Double = 0.0,
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class TransactionViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun loadCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCartLoading = true, errorMessage = null) }
            repository.getCart()
                .onSuccess { cartWithItems ->
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            cart = cartWithItems.cart,
                            cartItems = cartWithItems.items,
                            cartSubtotal = cartWithItems.subtotal,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            errorMessage = error.message ?: "Gagal memuat keranjang"
                        )
                    }
                }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCartLoading = true, errorMessage = null, successMessage = null) }
            repository.addToCart(product, quantity)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            successMessage = "Produk ditambahkan ke keranjang"
                        )
                    }
                    loadCart()
                    onDone?.invoke()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            errorMessage = error.message ?: "Gagal menambah item"
                        )
                    }
                }
        }
    }

    fun updateCartItem(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeCartItem(itemId)
            return
        }

        viewModelScope.launch {
            repository.updateCartItem(itemId, quantity)
                .onSuccess {
                    loadCart()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Gagal memperbarui quantity")
                    }
                }
        }
    }

    fun removeCartItem(itemId: String) {
        viewModelScope.launch {
            repository.removeCartItem(itemId)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Item dihapus dari keranjang") }
                    loadCart()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Gagal menghapus item")
                    }
                }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCartLoading = true, errorMessage = null) }
            repository.clearCart()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            cartItems = emptyList(),
                            cartSubtotal = 0.0,
                            successMessage = "Keranjang dikosongkan"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCartLoading = false,
                            errorMessage = error.message ?: "Gagal mengosongkan keranjang"
                        )
                    }
                }
        }
    }

    fun checkout(onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckoutLoading = true, errorMessage = null, successMessage = null) }
            repository.checkout()
                .onSuccess { order ->
                    _uiState.update {
                        it.copy(
                            isCheckoutLoading = false,
                            selectedOrder = order,
                            cartItems = emptyList(),
                            cartSubtotal = 0.0,
                            successMessage = "Checkout berhasil"
                        )
                    }
                    loadOrders()
                    onSuccess(order.id)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCheckoutLoading = false,
                            errorMessage = error.message ?: "Checkout gagal"
                        )
                    }
                }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOrdersLoading = true, errorMessage = null) }
            repository.getOrders()
                .onSuccess { orders ->
                    _uiState.update {
                        it.copy(
                            isOrdersLoading = false,
                            orders = orders,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isOrdersLoading = false,
                            errorMessage = error.message ?: "Gagal memuat pesanan"
                        )
                    }
                }
        }
    }

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isOrdersLoading = true, errorMessage = null) }
            repository.getOrderById(orderId)
                .onSuccess { order ->
                    _uiState.update {
                        it.copy(
                            isOrdersLoading = false,
                            selectedOrder = order,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isOrdersLoading = false,
                            errorMessage = error.message ?: "Gagal memuat detail order"
                        )
                    }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
