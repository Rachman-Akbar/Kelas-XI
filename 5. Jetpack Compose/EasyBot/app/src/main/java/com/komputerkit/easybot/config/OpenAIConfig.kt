package com.komputerkit.easybot.config

object OpenAIConfig {
    // ⚠️ PENTING: Ganti dengan API Key Anda yang valid!
    // Dapatkan API key dari: https://platform.openai.com/api-keys
    const val API_KEY = "YOUR_OPENAI_API_KEY_HERE"
    
    // Model yang akan digunakan - pastikan model tersedia di akun Anda
    const val MODEL = "gpt-3.5-turbo"
    
    // Pengaturan respons
    const val MAX_TOKENS = 300 // Ditingkatkan untuk respons yang lebih lengkap
    const val TEMPERATURE = 0.7
    
    // Retry configuration untuk mengatasi rate limit
    const val MAX_RETRY_ATTEMPTS = 3
    const val INITIAL_RETRY_DELAY = 5000L // Ditingkatkan jadi 5 detik untuk rate limit
    const val RETRY_MULTIPLIER = 3.0 // Exponential backoff lebih agresif
    
    // Request limiting - ditingkatkan untuk menghindari rate limit
    const val MIN_REQUEST_INTERVAL = 15000L // 15 detik antar request untuk mencegah rate limit
    
    // Rate limit specific delays
    const val RATE_LIMIT_DELAY = 30000L     // 30 detik setelah rate limit error
    
    // Input validation
    const val MAX_MESSAGE_LENGTH = 1000 // Ditingkatkan jadi 1000 karakter
    const val MAX_CONVERSATION_LENGTH = 8 // Dikurangi untuk menghemat token
    
    // API Base URL
    const val API_BASE_URL = "https://api.openai.com/"
    
    // Debugging
    const val DEBUG_MODE = true // Set false untuk production
}