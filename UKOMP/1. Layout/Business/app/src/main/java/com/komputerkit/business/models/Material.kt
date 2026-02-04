package com.komputerkit.business.models

data class Material(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val stock: Double = 0.0,
    val unit: String = "kg",
    val minStock: Double = 0.0,
    val purchasePrice: Double = 0.0,
    val supplier: String = "",
    val lastRestockDate: Long = System.currentTimeMillis(),
    val imageUrl: String = ""
) {
    val stockStatus: StockStatus
        get() = when {
            stock <= 0 -> StockStatus.OUT_OF_STOCK
            stock <= minStock -> StockStatus.LOW_STOCK
            else -> StockStatus.IN_STOCK
        }
}

enum class StockStatus {
    IN_STOCK,
    LOW_STOCK,
    OUT_OF_STOCK
}
