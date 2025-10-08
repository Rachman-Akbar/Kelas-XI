package com.komputerkit.easybot.utils

import com.komputerkit.easybot.config.OpenAIConfig
import com.komputerkit.easybot.data.model.ChatRequest
import com.komputerkit.easybot.data.model.Message
import com.komputerkit.easybot.network.NetworkModule
import kotlinx.coroutines.runBlocking

/**
 * Utility class untuk testing OpenAI API
 * Gunakan ini untuk memverifikasi API key bekerja sebelum menjalankan aplikasi
 */
object OpenAITester {
    
    fun testAPIKey() {
        runBlocking {
            try {
                val testRequest = ChatRequest(
                    model = "gpt-3.5-turbo",
                    messages = listOf(
                        Message(
                            role = "system",
                            content = "You are a helpful assistant."
                        ),
                        Message(
                            role = "user",
                            content = "Say 'Hello World' in Indonesian"
                        )
                    ),
                    max_tokens = 50,
                    temperature = 0.7
                )
                
                val response = NetworkModule.openAIService.getChatCompletion(
                    authorization = "Bearer ${OpenAIConfig.API_KEY}",
                    request = testRequest
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val messageContent = responseBody?.choices?.firstOrNull()?.message?.content
                    println("✅ API Test Success: $messageContent")
                } else {
                    println("❌ API Test Failed: ${response.code()} - ${response.message()}")
                    println("Response body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("❌ API Test Exception: ${e.message}")
            }
        }
    }
}