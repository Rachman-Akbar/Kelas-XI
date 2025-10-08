package com.komputerkit.easybot.utils

import com.komputerkit.easybot.config.OpenAIConfig

object MessageValidator {
    
    fun validateMessage(message: String): ValidationResult {
        return when {
            message.isBlank() -> ValidationResult.Invalid("Pesan tidak boleh kosong")
            message.length > OpenAIConfig.MAX_MESSAGE_LENGTH -> 
                ValidationResult.Invalid("Pesan terlalu panjang (maksimal ${OpenAIConfig.MAX_MESSAGE_LENGTH} karakter)")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateRateLimit(lastRequestTime: Long): ValidationResult {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRequest = currentTime - lastRequestTime
        
        return if (timeSinceLastRequest < OpenAIConfig.MIN_REQUEST_INTERVAL) {
            val remainingTime = (OpenAIConfig.MIN_REQUEST_INTERVAL - timeSinceLastRequest) / 1000
            ValidationResult.Invalid("Tunggu ${remainingTime} detik lagi sebelum mengirim pesan")
        } else {
            ValidationResult.Valid
        }
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
}