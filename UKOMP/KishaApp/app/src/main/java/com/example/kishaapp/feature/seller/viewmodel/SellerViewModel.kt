package com.example.kishaapp.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kishaapp.data.model.Product
import com.example.kishaapp.data.repository.MarketplaceRepository
import com.example.kishaapp.data.repository.StorageRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SellerUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val myProducts: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SellerViewModel(
    private val marketplaceRepository: MarketplaceRepository = MarketplaceRepository(),
    private val storageRepository: StorageRepository = StorageRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerUiState())
    val uiState: StateFlow<SellerUiState> = _uiState.asStateFlow()

    fun observeMyProducts(sellerId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            marketplaceRepository.observeProducts().collect { result ->
                result
                    .onSuccess { products ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                myProducts = products.filter { p -> p.sellerId == sellerId }
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memuat produk seller") }
                    }
            }
        }
    }

    fun addOrUpdateProduct(
        existingId: String?,
        title: String,
        description: String,
        price: Double,
        category: String,
        location: String,
        type: String,
        sellerId: String,
        sellerName: String,
        imageUri: Uri?,
        oldImageUrl: String = ""
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

            val imageUrl = if (imageUri != null) {
                val uploadResult = storageRepository.uploadProductImage(imageUri)
                val uploaded = uploadResult.getOrElse { error ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = error.message ?: "Upload gambar gagal") }
                    return@launch
                }
                uploaded
            } else {
                oldImageUrl
            }

            val product = Product(
                id = existingId.orEmpty(),
                title = title,
                description = description,
                price = price,
                imageUrl = imageUrl,
                imageUrls = if (imageUrl.isBlank()) emptyList() else listOf(imageUrl),
                category = category,
                sellerId = sellerId,
                sellerName = sellerName,
                location = location,
                type = type,
                createdAt = Timestamp.now()
            )

            val result = if (existingId.isNullOrBlank()) {
                marketplaceRepository.addProduct(product)
            } else {
                marketplaceRepository.updateProduct(product)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, successMessage = "Produk berhasil disimpan") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Simpan produk gagal") }
                }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            marketplaceRepository.deleteProduct(productId)
                .onSuccess {
                    _uiState.update { it.copy(successMessage = "Produk berhasil dihapus") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Hapus produk gagal") }
                }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
