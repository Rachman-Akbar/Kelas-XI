package com.komputerkit.business.models

data class Sale(
    val id: String = "",
    val invoiceNumber: String = "",
    val date: Long = System.currentTimeMillis(),
    val items: List<SaleItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val customerName: String = "",
    val status: SaleStatus = SaleStatus.COMPLETED
) {
    val totalProfit: Double
        get() = items.sumOf { it.profit }
}

data class SaleItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val cost: Double = 0.0,
    val subtotal: Double = 0.0
) {
    val profit: Double
        get() = (price - cost) * quantity
}

enum class PaymentMethod {
    CASH,
    TRANSFER,
    DEBIT,
    CREDIT,
    E_WALLET
}

enum class SaleStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    REFUNDED
}
