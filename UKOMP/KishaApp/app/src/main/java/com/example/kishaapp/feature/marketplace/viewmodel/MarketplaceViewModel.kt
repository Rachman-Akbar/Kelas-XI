package com.example.kishaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kishaapp.data.model.Category
import com.example.kishaapp.data.model.Product
import com.example.kishaapp.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarketplaceUiState(
    val isLoading: Boolean = true,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "All",
    val query: String = "",
    val errorMessage: String? = null
)

class MarketplaceViewModel(
    private val repository: MarketplaceRepository = MarketplaceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketplaceUiState())
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    init {
        observeProducts()
        observeCategories()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            repository.observeProducts().collect { result ->
                result
                    .onSuccess { products ->
                        _uiState.update { current ->
                            current.copy(
                                isLoading = false,
                                products = products,
                                filteredProducts = applyFilters(products, current.query, current.selectedCategory),
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat produk") }
                    }
            }
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            repository.observeCategories().collect { result ->
                result.onSuccess { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { current ->
            current.copy(
                query = query,
                filteredProducts = applyFilters(current.products, query, current.selectedCategory)
            )
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                filteredProducts = applyFilters(current.products, current.query, category)
            )
        }
    }

    fun getProductById(productId: String): Product? {
        return _uiState.value.products.firstOrNull { it.id == productId }
    }

    private fun applyFilters(products: List<Product>, query: String, category: String): List<Product> {
        return products.filter { product ->
            val queryMatch = query.isBlank() || product.title.contains(query, ignoreCase = true)
            val categoryMatch = category == "All" || product.category.equals(category, ignoreCase = true)
            queryMatch && categoryMatch
        }
    }
}
