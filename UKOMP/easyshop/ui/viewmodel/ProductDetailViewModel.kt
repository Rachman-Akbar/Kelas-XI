package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.repository.ProductRepository
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _productState = MutableStateFlow(ProductDetailState())
    val productState: StateFlow<ProductDetailState> = _productState.asStateFlow()
    
    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _productState.value = _productState.value.copy(isLoading = true, error = null)
            
            when (val result = productRepository.getProductById(productId)) {
                is Resource.Success -> {
                    _productState.value = _productState.value.copy(
                        product = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _productState.value = _productState.value.copy(
                        product = null,
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _productState.value = _productState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _productState.value = _productState.value.copy(error = null)
    }
}