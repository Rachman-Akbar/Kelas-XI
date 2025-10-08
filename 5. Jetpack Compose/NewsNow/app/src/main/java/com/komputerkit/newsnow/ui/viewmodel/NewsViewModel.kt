package com.komputerkit.newsnow.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.newsnow.data.api.RetrofitInstance
import com.komputerkit.newsnow.data.model.Article
import com.komputerkit.newsnow.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    
    private val repository = NewsRepository(RetrofitInstance.api)
    
    // State untuk daftar artikel
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    
    // State untuk loading
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    // State untuk error
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage
    
    // State untuk kategori yang dipilih
    private val _selectedCategory = mutableStateOf("general")
    val selectedCategory: State<String> = _selectedCategory
    
    // State untuk query pencarian
    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery
    
    init {
        fetchTopHeadlines()
    }
    
    fun fetchTopHeadlines(category: String = "general") {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedCategory.value = category
            
            try {
                val response = repository.getTopHeadlines(category = category)
                if (response.isSuccessful) {
                    _articles.value = response.body()?.articles ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load news: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun fetchEverything(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _searchQuery.value = query
            
            try {
                val response = repository.searchNews(query)
                if (response.isSuccessful) {
                    _articles.value = response.body()?.articles ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search news: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}