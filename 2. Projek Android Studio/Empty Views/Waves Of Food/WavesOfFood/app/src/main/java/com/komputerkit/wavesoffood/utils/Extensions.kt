package com.komputerkit.wavesoffood.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension functions untuk formatting
 */

/**
 * Format price to Indonesian Rupiah
 */
fun Double.toRupiah(): String {
    return "Rp ${String.format("%,.0f", this)}"
}

/**
 * Format date to readable string
 */
fun Date.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return sdf.format(this)
}

/**
 * Format date (short version without time)
 */
fun Date.toShortDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(this)
}

/**
 * Shorten string for display (e.g., Order ID)
 */
fun String.shortened(length: Int = Constants.ORDER_ID_DISPLAY_LENGTH): String {
    return if (this.length > length) {
        this.substring(0, length)
    } else {
        this
    }
}

/**
 * Validate email format
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if string is empty or blank
 */
fun String?.isNullOrEmpty(): Boolean {
    return this == null || this.trim().isEmpty()
}
