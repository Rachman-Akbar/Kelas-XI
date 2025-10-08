package com.komputerkit.easybot.utils

import com.komputerkit.easybot.config.OpenAIConfig
import com.komputerkit.easybot.data.model.ChatRequest
import com.komputerkit.easybot.data.model.Message
import com.komputerkit.easybot.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object APIKeyValidator {
    
    suspend fun validateAPIKey(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val testRequest = ChatRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        Message(
                            role = "user",
                            content = "Test"
                        )
                    ),
                    max_tokens = 5,
                    temperature = 0.0
                )
                
                val response = NetworkModule.openAIService.getChatCompletion(
                    authorization = "Bearer ${OpenAIConfig.API_KEY}",
                    request = testRequest
                )
                
                when (response.code()) {
                    200 -> Result.success(true)
                    401 -> Result.failure(Exception("API Key tidak valid atau expired"))
                    429 -> Result.failure(Exception("Rate limit tercapai, coba lagi nanti"))
                    else -> Result.failure(Exception("Error validasi API: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal menghubungi server OpenAI: ${e.message}"))
            }
        }
    }
}