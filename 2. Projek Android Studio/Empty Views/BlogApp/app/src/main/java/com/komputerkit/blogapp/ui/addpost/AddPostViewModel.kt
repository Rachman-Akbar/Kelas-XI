package com.komputerkit.blogapp.ui.addpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.blogapp.model.Blog
import com.komputerkit.blogapp.service.AuthService
import com.komputerkit.blogapp.service.FirestoreService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddPostViewModel : ViewModel() {
    
    private val firestoreService = FirestoreService()
    private val authService = AuthService()
    
    private val _addPostState = MutableStateFlow<AddPostState>(AddPostState.Idle)
    val addPostState: StateFlow<AddPostState> = _addPostState.asStateFlow()
    
    fun publishPost(title: String, content: String) {
        viewModelScope.launch {
            _addPostState.value = AddPostState.Loading
            
            val currentUser = authService.currentUser
            if (currentUser == null) {
                _addPostState.value = AddPostState.Error("User tidak terautentikasi")
                return@launch
            }
            
            val blog = Blog(
                title = title,
                content = content,
                authorId = currentUser.uid,
                authorName = currentUser.email ?: "Anonim",
                timestamp = System.currentTimeMillis()
            )
            
            val result = firestoreService.addBlogPost(blog)
            
            if (result.isSuccess) {
                _addPostState.value = AddPostState.Success
            } else {
                _addPostState.value = AddPostState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Gagal mempublish blog"
                )
            }
        }
    }
    
    sealed class AddPostState {
        object Idle : AddPostState()
        object Loading : AddPostState()
        object Success : AddPostState()
        data class Error(val message: String) : AddPostState()
    }
}