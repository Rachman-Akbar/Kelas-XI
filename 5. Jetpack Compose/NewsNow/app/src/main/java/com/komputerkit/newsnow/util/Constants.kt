package com.komputerkit.newsnow.util

object Constants {
    // Ganti dengan API Key Anda dari https://newsapi.org/
    // Daftar gratis di: https://newsapi.org/register
    const val NEWS_API_KEY = "435275d410b448928701e2d523242a64"
    
    // Kategori berita yang tersedia
    val NEWS_CATEGORIES = listOf(
        "general",
        "business", 
        "entertainment",
        "health",
        "science",
        "sports",
        "technology"
    )
    
    // Country codes untuk News API
    const val DEFAULT_COUNTRY = "us"
    const val DEFAULT_CATEGORY = "general"
    const val DEFAULT_PAGE_SIZE = 20
}