package com.komputerkit.socialmedia.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.socialmedia.managers.PreloadedImageManager
import com.komputerkit.socialmedia.models.ImageCategory
import com.komputerkit.socialmedia.models.PreloadedImage
import com.komputerkit.socialmedia.models.StoryTemplate
import kotlinx.coroutines.launch

class PreloadedImageViewModel : ViewModel() {
    
    private val imageManager = PreloadedImageManager()
    
    private val _preloadedImages = MutableLiveData<List<PreloadedImage>>()
    val preloadedImages: LiveData<List<PreloadedImage>> = _preloadedImages
    
    private val _categories = MutableLiveData<List<ImageCategory>>()
    val categories: LiveData<List<ImageCategory>> = _categories
    
    private val _storyTemplates = MutableLiveData<List<StoryTemplate>>()
    val storyTemplates: LiveData<List<StoryTemplate>> = _storyTemplates
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private var allImages = listOf<PreloadedImage>()
    private var currentCategory: String? = null
    
    fun loadPreloadedImages(type: String, category: String? = null) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                
                val images = imageManager.getPreloadedImages(type, category)
                allImages = images
                currentCategory = category
                _preloadedImages.value = images
                
            } catch (e: Exception) {
                _error.value = "Failed to load images: ${e.message}"
                _preloadedImages.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = imageManager.getImageCategories()
                _categories.value = categories
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            }
        }
    }
    
    fun loadStoryTemplates() {
        viewModelScope.launch {
            try {
                val templates = imageManager.getStoryTemplates()
                _storyTemplates.value = templates
            } catch (e: Exception) {
                _error.value = "Failed to load templates: ${e.message}"
            }
        }
    }
    
    fun filterByCategory(category: String?) {
        currentCategory = category
        _preloadedImages.value = if (category == null) {
            allImages
        } else {
            allImages.filter { it.category == category }
        }
    }
    
    fun searchImages(query: String) {
        val filteredImages = allImages.filter { image ->
            image.title.contains(query, ignoreCase = true) ||
            image.description.contains(query, ignoreCase = true) ||
            image.category.contains(query, ignoreCase = true) ||
            image.tags.any { it.contains(query, ignoreCase = true) }
        }
        
        _preloadedImages.value = filteredImages
    }
    
    fun clearError() {
        _error.value = null
    }
}