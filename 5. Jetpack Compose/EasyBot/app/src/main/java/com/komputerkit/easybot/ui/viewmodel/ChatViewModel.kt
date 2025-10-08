package com.komputerkit.easybot.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.easybot.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        // Add welcome message
        addMessage(
            ChatMessage(
                content = "Halo! Saya EasyBot, asisten AI Anda. Ada yang bisa saya bantu?",
                isFromUser = false
            )
        )
    }
    
    fun sendMessage(message: String) {
        if (message.isBlank()) return
        
        Log.d("ChatViewModel", "📤 Sending message: '$message'")
        
        // Add user message
        addMessage(ChatMessage(content = message, isFromUser = true))
        
        // Set loading state
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            Log.d("ChatViewModel", "🔄 Starting repository call")
            repository.sendMessage(message)
                .onSuccess { response ->
                    Log.d("ChatViewModel", "✅ Repository success: '$response'")
                    addMessage(ChatMessage(content = response, isFromUser = false))
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { error ->
                    Log.e("ChatViewModel", "❌ Repository failure: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Terjadi kesalahan: ${error.message}"
                    )
                }
        }
    }
    
    private fun addMessage(message: ChatMessage) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + message
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}