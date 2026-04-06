package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.repository.FavoriteRepository
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteState(
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

data class FavoriteToggleState(
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false
)

class FavoriteViewModel constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _favoriteState = MutableStateFlow(FavoriteState())
    val favoriteState: StateFlow<FavoriteState> = _favoriteState.asStateFlow()
    
    private val _favoriteToggleStates = MutableStateFlow<Map<String, FavoriteToggleState>>(emptyMap())
    val favoriteToggleStates: StateFlow<Map<String, FavoriteToggleState>> = _favoriteToggleStates.asStateFlow()
    
    init {
        loadFavorites()
    }
    
    fun loadFavorites() {
        viewModelScope.launch {
            _favoriteState.value = _favoriteState.value.copy(isLoading = true, error = null)
            
            when (val result = favoriteRepository.getFavoriteProducts()) {
                is Resource.Success -> {
                    _favoriteState.value = _favoriteState.value.copy(
                        favorites = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _favoriteState.value = _favoriteState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _favoriteState.value = _favoriteState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun checkFavoriteStatus(productId: String) {
        viewModelScope.launch {
            val currentStates = _favoriteToggleStates.value.toMutableMap()
            currentStates[productId] = FavoriteToggleState(isLoading = true)
            _favoriteToggleStates.value = currentStates
            
            when (val result = favoriteRepository.isProductFavorite(productId)) {
                is Resource.Success -> {
                    currentStates[productId] = FavoriteToggleState(
                        isFavorite = result.data ?: false,
                        isLoading = false
                    )
                    _favoriteToggleStates.value = currentStates
                }
                is Resource.Error -> {
                    currentStates[productId] = FavoriteToggleState(isLoading = false)
                    _favoriteToggleStates.value = currentStates
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val currentStates = _favoriteToggleStates.value.toMutableMap()
            val currentState = currentStates[productId] ?: FavoriteToggleState()
            
            if (currentState.isLoading) return@launch
            
            currentStates[productId] = currentState.copy(isLoading = true)
            _favoriteToggleStates.value = currentStates
            
            val result = if (currentState.isFavorite) {
                favoriteRepository.removeFromFavorites(productId)
            } else {
                favoriteRepository.addToFavorites(productId)
            }
            
            when (result) {
                is Resource.Success -> {
                    currentStates[productId] = currentState.copy(
                        isFavorite = !currentState.isFavorite,
                        isLoading = false
                    )
                    _favoriteToggleStates.value = currentStates
                    
                    val message = if (!currentState.isFavorite) {
                        "Added to favorites"
                    } else {
                        "Removed from favorites"
                    }
                    
                    _favoriteState.value = _favoriteState.value.copy(successMessage = message)
                    
                    // Reload favorites list if we're viewing favorites screen
                    loadFavorites()
                }
                is Resource.Error -> {
                    currentStates[productId] = currentState.copy(isLoading = false)
                    _favoriteToggleStates.value = currentStates
                    
                    _favoriteState.value = _favoriteState.value.copy(error = result.message)
                }
                is Resource.Loading -> {
                    // Already handled above
                }
            }
        }
    }
    
    fun clearMessages() {
        _favoriteState.value = _favoriteState.value.copy(
            error = null,
            successMessage = null
        )
    }
}