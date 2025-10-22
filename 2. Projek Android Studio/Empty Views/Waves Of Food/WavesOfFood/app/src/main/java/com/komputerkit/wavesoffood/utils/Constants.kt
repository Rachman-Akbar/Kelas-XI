package com.komputerkit.wavesoffood.utils

/**
 * Constants untuk aplikasi WavesOfFood
 */
object Constants {
    
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_ORDERS = "orders"
    
    // Intent Extras
    const val EXTRA_PRODUCT_ID = "PRODUCT_ID"
    const val EXTRA_ORDER_ID = "ORDER_ID"
    const val EXTRA_SUBTOTAL = "SUBTOTAL"
    
    // Product Categories
    const val CATEGORY_ALL = "All"
    const val CATEGORY_FOOD = "Food"
    const val CATEGORY_DRINK = "Drink"
    const val CATEGORY_SNACK = "Snack"
    
    // Order Status
    const val ORDER_STATUS_PENDING = "Pending"
    const val ORDER_STATUS_PROCESSING = "Processing"
    const val ORDER_STATUS_COMPLETED = "Completed"
    const val ORDER_STATUS_CANCELLED = "Cancelled"
    
    // Tax Rate
    const val TAX_RATE = 0.10 // 10%
    
    // Payment Simulation Delay
    const val PAYMENT_DELAY_MS = 3000L // 3 seconds
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val ORDER_ID_DISPLAY_LENGTH = 8
}
