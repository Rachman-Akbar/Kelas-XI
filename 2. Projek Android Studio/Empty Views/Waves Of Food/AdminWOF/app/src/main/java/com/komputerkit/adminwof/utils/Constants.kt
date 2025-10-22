package com.komputerkit.adminwof.utils

/**
 * Constants untuk Admin App
 */
object Constants {
    // Firebase Collections
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_USERS = "users"
    
    // Admin Email (Hardcoded for now)
    val ADMIN_EMAILS = setOf(
        "admin@wavesoffood.com",
        "admin@komputerkit.com"
    )
    
    // Storage
    const val STORAGE_PRODUCTS = "products"
    
    // Product Categories
    const val CATEGORY_MAIN_COURSE = "Main Course"
    const val CATEGORY_APPETIZER = "Appetizer"
    const val CATEGORY_DESSERT = "Dessert"
    const val CATEGORY_BEVERAGE = "Beverage"
    const val CATEGORY_SNACK = "Snack"
    
    val CATEGORIES = listOf(
        CATEGORY_MAIN_COURSE,
        CATEGORY_APPETIZER,
        CATEGORY_DESSERT,
        CATEGORY_BEVERAGE,
        CATEGORY_SNACK
    )
    
    // Order Status
    const val ORDER_STATUS_ORDERED = "Ordered"
    const val ORDER_STATUS_PROCESSING = "Processing"
    const val ORDER_STATUS_SHIPPED = "Shipped"
    const val ORDER_STATUS_DELIVERED = "Delivered"
    const val ORDER_STATUS_CANCELLED = "Cancelled"
    
    val ORDER_STATUSES = listOf(
        ORDER_STATUS_ORDERED,
        ORDER_STATUS_PROCESSING,
        ORDER_STATUS_SHIPPED,
        ORDER_STATUS_DELIVERED,
        ORDER_STATUS_CANCELLED
    )
    
    // Intent Extras
    const val EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID"
    const val EXTRA_ORDER_ID = "EXTRA_ORDER_ID"
}
