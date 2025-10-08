package com.komputerkit.blogapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.blogapp.model.Blog
import com.komputerkit.blogapp.service.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    
    private val firestoreService = FirestoreService()
    
    private val _blogs = MutableStateFlow<List<Blog>>(emptyList())
    val blogs: StateFlow<List<Blog>> = _blogs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadBlogs()
    }
    
    private fun loadBlogs() {
        viewModelScope.launch {
            _isLoading.value = true
            
            firestoreService.getAllBlogs()
                .catch { e ->
                    _error.value = e.localizedMessage ?: "Terjadi kesalahan"
                    _isLoading.value = false
                }
                .collect { blogList ->
                    _blogs.value = blogList
                    _isLoading.value = false
                    _error.value = null
                }
        }
    }
    
    fun refreshBlogs() {
        loadBlogs()
    }
}