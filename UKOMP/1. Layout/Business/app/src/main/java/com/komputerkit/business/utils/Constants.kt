package com.komputerkit.business.utils

object Constants {
    // App
    const val APP_NAME = "UMKM Business"
    const val APP_VERSION = "1.0.0"
    
    // Shared Preferences
    const val PREFS_NAME = "umkm_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    // Pagination
    const val PAGE_SIZE = 20
    
    // Categories
    val PRODUCT_CATEGORIES = listOf(
        "Minuman",
        "Makanan",
        "Snack",
        "Kue",
        "Roti",
        "Lainnya"
    )
    
    val MATERIAL_CATEGORIES = listOf(
        "Biji Kopi",
        "Susu",
        "Pemanis",
        "Tepung",
        "Bahan Tambahan",
        "Kemasan",
        "Lainnya"
    )
    
    val PAYMENT_METHODS = listOf(
        "Tunai",
        "Transfer Bank",
        "Debit",
        "Kredit",
        "E-Wallet"
    )
}
