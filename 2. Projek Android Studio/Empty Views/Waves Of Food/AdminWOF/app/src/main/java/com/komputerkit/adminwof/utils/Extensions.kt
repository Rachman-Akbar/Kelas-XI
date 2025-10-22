package com.komputerkit.adminwof.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Extension functions untuk formatting
 */

/**
 * Format Double ke Rupiah
 */
fun Double.toRupiah(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return formatter.format(this)
}

/**
 * Format Date ke String
 */
fun Date.toFormattedDate(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(this)
}

/**
 * Shorten string untuk ID panjang
 */
fun String.shortened(maxLength: Int = 20): String {
    return if (this.length > maxLength) {
        "${this.substring(0, maxLength)}..."
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
