package com.komputerkit.business.models

data class Product(
    val id: String = "",
    val name: String = "",
    val sku: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val sellingPrice: Double = 0.0,
    val costOfGoodsPrice: Double = 0.0,
    val profitMargin: Double = 0.0,
    val unit: String = "pcs",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val profit: Double
        get() = sellingPrice - costOfGoodsPrice
    
    val profitPercentage: Double
        get() = if (costOfGoodsPrice > 0) (profit / costOfGoodsPrice) * 100 else 0.0
}
