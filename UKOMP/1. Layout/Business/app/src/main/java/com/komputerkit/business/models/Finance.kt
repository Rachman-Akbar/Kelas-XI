package com.komputerkit.business.models

data class Finance(
    val id: String = "",
    val type: TransactionType = TransactionType.INCOME,
    val category: String = "",
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val description: String = "",
    val reference: String = ""
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netProfit: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0
) {
    val profitMargin: Double
        get() = if (totalIncome > 0) (netProfit / totalIncome) * 100 else 0.0
}
