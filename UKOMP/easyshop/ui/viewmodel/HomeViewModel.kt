package com.komputerkit.easyshop.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.easyshop.data.model.Banner
import com.komputerkit.easyshop.data.model.Category
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.repository.HomeRepository
import com.komputerkit.easyshop.data.repository.ProductRepository
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val userName: String = "",
    val isUserNameLoading: Boolean = false,
    val userNameError: String? = null,
    
    val banners: List<Banner> = emptyList(),
    val isBannersLoading: Boolean = false,
    val bannersError: String? = null,
    
    val categories: List<Category> = emptyList(),
    val isCategoriesLoading: Boolean = false,
    val categoriesError: String? = null,
    
    val topProducts: List<Product> = emptyList(),
    val isTopProductsLoading: Boolean = false,
    val topProductsError: String? = null
)

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val productRepository: ProductRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        loadUserName()
        loadBanners()
        loadCategories()
        loadTopProducts()
    }
    
    private fun loadUserName() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                _homeState.value = _homeState.value.copy(isUserNameLoading = true, userNameError = null)
                
                when (val result = homeRepository.getUserName(currentUser.uid)) {
                    is Resource.Success -> {
                        _homeState.value = _homeState.value.copy(
                            userName = result.data ?: "User",
                            isUserNameLoading = false,
                            userNameError = null
                        )
                    }
                    is Resource.Error -> {
                        _homeState.value = _homeState.value.copy(
                            userName = "User", // Fallback
                            isUserNameLoading = false,
                            userNameError = result.message
                        )
                    }
                    is Resource.Loading -> {
                        _homeState.value = _homeState.value.copy(isUserNameLoading = true)
                    }
                }
            }
        }
    }
    
    private fun loadBanners() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isBannersLoading = true, bannersError = null)
            
            when (val result = homeRepository.getBanners()) {
                is Resource.Success -> {
                    _homeState.value = _homeState.value.copy(
                        banners = result.data ?: emptyList(),
                        isBannersLoading = false,
                        bannersError = null
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        banners = emptyList(),
                        isBannersLoading = false,
                        bannersError = result.message
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isBannersLoading = true)
                }
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isCategoriesLoading = true, categoriesError = null)
            
            when (val result = homeRepository.getCategories()) {
                is Resource.Success -> {
                    _homeState.value = _homeState.value.copy(
                        categories = result.data ?: emptyList(),
                        isCategoriesLoading = false,
                        categoriesError = null
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        categories = emptyList(),
                        isCategoriesLoading = false,
                        categoriesError = result.message
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isCategoriesLoading = true)
                }
            }
        }
    }
    
    fun refreshData() {
        loadHomeData()
    }
    
    fun clearBannersError() {
        _homeState.value = _homeState.value.copy(bannersError = null)
    }
    
    fun clearCategoriesError() {
        _homeState.value = _homeState.value.copy(categoriesError = null)
    }
    
    fun clearUserNameError() {
        _homeState.value = _homeState.value.copy(userNameError = null)
    }
    
    fun clearTopProductsError() {
        _homeState.value = _homeState.value.copy(topProductsError = null)
    }
    
    private fun loadTopProducts() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isTopProductsLoading = true, topProductsError = null)
            
            when (val result = productRepository.getTopProducts(10)) {
                is Resource.Success -> {
                    _homeState.value = _homeState.value.copy(
                        topProducts = result.data ?: emptyList(),
                        isTopProductsLoading = false,
                        topProductsError = null
                    )
                }
                is Resource.Error -> {
                    _homeState.value = _homeState.value.copy(
                        topProducts = emptyList(),
                        isTopProductsLoading = false,
                        topProductsError = result.message
                    )
                }
                is Resource.Loading -> {
                    _homeState.value = _homeState.value.copy(isTopProductsLoading = true)
                }
            }
        }
    }
}