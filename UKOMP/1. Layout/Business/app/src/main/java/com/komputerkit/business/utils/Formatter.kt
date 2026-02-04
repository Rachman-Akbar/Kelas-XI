package com.komputerkit.business.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Formatter {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id", "ID"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    
    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount).replace("Rp", "Rp ")
    }
    
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
    
    fun formatNumber(number: Double, decimals: Int = 2): String {
        return String.format(Locale("id", "ID"), "%.${decimals}f", number)
    }
    
    fun formatPercentage(value: Double): String {
        return String.format(Locale("id", "ID"), "%.1f%%", value)
    }
}
