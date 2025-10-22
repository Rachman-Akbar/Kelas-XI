package com.komputerkit.adminwof.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.adminwof.data.model.ProductModel
import com.komputerkit.adminwof.data.repository.AdminRepository
import com.komputerkit.adminwof.utils.UiState
import kotlinx.coroutines.launch

/**
 * ViewModel untuk Product Management
 * Menggunakan AdminRepository untuk semua operasi CRUD
 */
class ProductViewModel(
    private val repository: AdminRepository
) : ViewModel() {
    
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products
    
    private val _product = MutableLiveData<ProductModel?>()
    val product: LiveData<ProductModel?> = _product
    
    private val _uiState = MutableLiveData<UiState<String>>(UiState.Idle)
    val uiState: LiveData<UiState<String>> = _uiState
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    /**
     * Fetch all products
     */
    fun fetchAllProducts() {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.fetchAllProducts()
            
            result.onSuccess { productList ->
                _products.value = productList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load products"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Fetch product by ID
     */
    fun fetchProductById(productId: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            val result = repository.fetchProductById(productId)
            
            result.onSuccess { productData ->
                _product.value = productData
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load product"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Upload product image dengan product name untuk filename
     */
    fun uploadProductImage(imageUri: Uri, productName: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val result = repository.uploadImageAndGetUrl(imageUri, productName)
            
            result.onSuccess { downloadUrl ->
                _uiState.value = UiState.Success("Image uploaded successfully")
                callback(downloadUrl)
            }.onFailure { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Failed to upload image"
                )
                callback(null)
            }
        }
    }
    
    /**
     * Create new product dengan validation
     */
    fun createProduct(product: ProductModel) {
        // Validate product data
        if (!repository.validateProduct(product)) {
            _uiState.value = UiState.Error("Please fill all required fields")
            return
        }
        
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            val result = repository.addProduct(product)
            
            result.onSuccess { productId ->
                _uiState.value = UiState.Success("Product created successfully with ID: $productId")
            }.onFailure { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Failed to create product"
                )
            }
        }
    }
    
    /**
     * Update existing product dengan validation
     */
    fun updateProduct(product: ProductModel) {
        // Validate product data
        if (!repository.validateProduct(product)) {
            _uiState.value = UiState.Error("Please fill all required fields")
            return
        }
        
        if (product.id.isEmpty()) {
            _uiState.value = UiState.Error("Product ID is required")
            return
        }
        
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            val result = repository.updateProduct(product)
            
            result.onSuccess {
                _uiState.value = UiState.Success("Product updated successfully")
            }.onFailure { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Failed to update product"
                )
            }
        }
    }
    
    /**
     * Delete product
     */
    fun deleteProduct(productId: String) {
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            val result = repository.deleteProduct(productId)
            
            result.onSuccess {
                _uiState.value = UiState.Success("Product deleted successfully")
            }.onFailure { exception ->
                _uiState.value = UiState.Error(
                    exception.message ?: "Failed to delete product"
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
