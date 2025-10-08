package com.komputerkit.easybot.data.repository

import android.util.Log
import com.komputerkit.easybot.config.OpenAIConfig
import com.komputerkit.easybot.data.model.ChatRequest
import com.komputerkit.easybot.data.model.Message
import com.komputerkit.easybot.network.NetworkModule
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChatRepository {
    private val apiService = NetworkModule.openAIService
    private var lastRequestTime = 0L
    
    companion object {
        private const val TAG = "ChatRepository"
        private var lastRateLimitTime = 0L // Track when we last got rate limited
    }
    
    suspend fun sendMessage(message: String): Result<String> {
        Log.d(TAG, "🚀 Starting sendMessage: '$message'")
        
        // Validate API key
        if (OpenAIConfig.API_KEY.isBlank() || 
            OpenAIConfig.API_KEY == "sk-proj-..." || 
            OpenAIConfig.API_KEY == "MASUKKAN_API_KEY_ANDA_DISINI") {
            Log.e(TAG, "❌ Invalid API key detected")
            return Result.failure(Exception("🔑 API Key tidak valid. Silakan masukkan API key yang valid di OpenAIConfig.kt"))
        }
        
        Log.d(TAG, "✅ API key validation passed")
        
        // Validate message length
        if (message.length > OpenAIConfig.MAX_MESSAGE_LENGTH) {
            Log.e(TAG, "❌ Message too long: ${message.length} chars")
            return Result.failure(Exception("📝 Pesan terlalu panjang. Maksimal ${OpenAIConfig.MAX_MESSAGE_LENGTH} karakter"))
        }
        
        // Rate limiting - ensure minimum interval between requests + rate limit penalty
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRequest = currentTime - lastRequestTime
        val timeSinceRateLimit = currentTime - lastRateLimitTime
        
        // If we were recently rate limited, apply longer delay
        if (lastRateLimitTime > 0 && timeSinceRateLimit < OpenAIConfig.RATE_LIMIT_DELAY) {
            val remainingDelay = OpenAIConfig.RATE_LIMIT_DELAY - timeSinceRateLimit
            Log.w(TAG, "⏰ Still in rate limit penalty period, waiting ${remainingDelay}ms more")
            delay(remainingDelay)
        }
        
        // Regular rate limiting
        if (timeSinceLastRequest < OpenAIConfig.MIN_REQUEST_INTERVAL) {
            val waitTime = OpenAIConfig.MIN_REQUEST_INTERVAL - timeSinceLastRequest
            Log.d(TAG, "⏰ Rate limiting: waiting ${waitTime}ms")
            delay(waitTime)
        }
        lastRequestTime = System.currentTimeMillis()
        
        return executeWithRetry(message)
    }
    
    private suspend fun executeWithRetry(message: String): Result<String> {
        var lastException: Exception? = null
        var isRateLimited = false
        
        repeat(OpenAIConfig.MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                if (OpenAIConfig.DEBUG_MODE) {
                    Log.d(TAG, "Attempt ${attempt + 1}/${OpenAIConfig.MAX_RETRY_ATTEMPTS}")
                }
                
                val result = makeApiCall(message)
                if (result.isSuccess) {
                    return result
                }
                
                val exception = result.exceptionOrNull() as? Exception
                lastException = exception
                
                // Check if it's a rate limit error
                if (exception?.message?.contains("429") == true || 
                    exception?.message?.contains("rate limit") == true) {
                    isRateLimited = true
                    Log.w(TAG, "Rate limit detected, applying longer delay")
                }
                
            } catch (e: Exception) {
                lastException = e
                if (OpenAIConfig.DEBUG_MODE) {
                    Log.e(TAG, "Attempt ${attempt + 1} failed: ${e.message}")
                }
            }
            
            // Wait before retry with special handling for rate limits
            if (attempt < OpenAIConfig.MAX_RETRY_ATTEMPTS - 1) {
                val baseDelay = if (isRateLimited) {
                    // Extra long delay for rate limits
                    OpenAIConfig.INITIAL_RETRY_DELAY * 3
                } else {
                    OpenAIConfig.INITIAL_RETRY_DELAY
                }
                
                val delayTime = (baseDelay * 
                    Math.pow(OpenAIConfig.RETRY_MULTIPLIER, attempt.toDouble())).toLong()
                    
                Log.d(TAG, "Waiting ${delayTime}ms before retry...")
                delay(delayTime)
            }
        }
        
        // All retries failed, return appropriate error message
        return Result.failure(getErrorMessage(lastException, isRateLimited))
    }
    
    private suspend fun makeApiCall(message: String): Result<String> {
        return try {
            val request = ChatRequest(
                model = OpenAIConfig.MODEL,
                messages = listOf(
                    Message(
                        role = "system", 
                        content = "Kamu adalah asisten AI yang membantu dan ramah. Jawab dalam bahasa Indonesia kecuali diminta sebaliknya."
                    ),
                    Message(role = "user", content = message)
                ),
                max_tokens = OpenAIConfig.MAX_TOKENS,
                temperature = OpenAIConfig.TEMPERATURE
            )
            
            val response = apiService.getChatCompletion(
                authorization = "Bearer ${OpenAIConfig.API_KEY}",
                request = request
            )
            
            Log.d(TAG, "📡 Response received: ${response.code()}")
            
            when {
                response.isSuccessful -> {
                    Log.d(TAG, "✅ Response successful")
                    val chatResponse = response.body()
                    Log.d(TAG, "📦 Response body: ${chatResponse?.toString()}")
                    val content = chatResponse?.choices?.firstOrNull()?.message?.content
                    Log.d(TAG, "💬 Extracted content: '$content'")
                    if (content != null && content.isNotBlank()) {
                        Log.d(TAG, "🎉 Returning successful result")
                        Result.success(content.trim())
                    } else {
                        Log.e(TAG, "❌ Content is null or empty")
                        Result.failure(Exception("❌ Tidak ada respons dari server"))
                    }
                }
                response.code() == 401 -> {
                    Log.e(TAG, "🔑 Unauthorized - API key issue")
                    Result.failure(Exception("🔑 API Key tidak valid atau sudah kedaluwarsa"))
                }
                response.code() == 429 -> {
                    Log.e(TAG, "⏰ Rate limited")
                    lastRateLimitTime = System.currentTimeMillis() // Record rate limit time
                    Result.failure(Exception("⏰ Terlalu banyak permintaan. Silakan tunggu sebentar"))
                }
                response.code() == 500 -> {
                    Log.e(TAG, "🔧 Server error")
                    Result.failure(Exception("🔧 Server OpenAI sedang bermasalah. Coba lagi nanti"))
                }
                else -> {
                    Log.e(TAG, "❌ Unknown error: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error body: $errorBody")
                    Result.failure(Exception("❌ Error: ${response.code()} - ${response.message()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getErrorMessage(exception: Exception?, isRateLimited: Boolean): Exception {
        return when {
            isRateLimited -> Exception("⏰ Rate limit terlampaui. Tunggu beberapa saat sebelum mengirim pesan lagi")
            exception is UnknownHostException -> {
                Exception("🌐 Tidak ada koneksi internet. Periksa koneksi Anda")
            }
            exception is SocketTimeoutException -> {
                Exception("⏱️ Koneksi timeout. Server mungkin lambat, coba lagi")
            }
            else -> {
                Exception("❌ ${exception?.message ?: "Terjadi kesalahan tidak dikenal"}")
            }
        }
    }
}