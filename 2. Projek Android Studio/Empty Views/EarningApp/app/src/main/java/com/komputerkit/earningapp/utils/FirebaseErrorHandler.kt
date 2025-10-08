package com.komputerkit.earningapp.utils

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException

object FirebaseErrorHandler {
    private const val TAG = "FirebaseErrorHandler"
    
    fun handleAuthException(e: Exception): String {
        Log.e(TAG, "=== Firebase Auth Exception ===")
        Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
        Log.e(TAG, "Exception message: ${e.message}")
        Log.e(TAG, "Exception cause: ${e.cause}")
        
        return when (e) {
            is FirebaseAuthException -> {
                Log.e(TAG, "Firebase Auth Error Code: ${e.errorCode}")
                when (e.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Format email tidak valid."
                    "ERROR_WEAK_PASSWORD" -> "Password terlalu lemah. Minimal 6 karakter."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "Email sudah terdaftar. Gunakan email lain."
                    "ERROR_OPERATION_NOT_ALLOWED" -> "Pendaftaran tidak diizinkan. Hubungi administrator."
                    "ERROR_USER_DISABLED" -> "Akun telah dinonaktifkan."
                    "ERROR_TOO_MANY_REQUESTS" -> "Terlalu banyak percobaan. Coba lagi nanti."
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Tidak ada koneksi internet."
                    else -> "Error Firebase Auth: ${e.errorCode} - ${e.message}"
                }
            }
            is FirebaseException -> {
                Log.e(TAG, "Firebase Exception: ${e.message}")
                "Error Firebase: ${e.message}"
            }
            else -> {
                val message = e.message ?: "Unknown error"
                when {
                    message.contains("not recognized", ignoreCase = true) -> 
                        "Firebase tidak dikenali. Periksa konfigurasi aplikasi."
                    message.contains("network", ignoreCase = true) -> 
                        "Tidak ada koneksi internet."
                    message.contains("timeout", ignoreCase = true) -> 
                        "Koneksi timeout. Coba lagi."
                    else -> "Error: $message"
                }
            }
        }
    }
    
    fun logDetailedError(tag: String, operation: String, e: Exception) {
        Log.e(tag, "=== Detailed Error Log ===")
        Log.e(tag, "Operation: $operation")
        Log.e(tag, "Exception type: ${e.javaClass.name}")
        Log.e(tag, "Exception message: ${e.message}")
        Log.e(tag, "Exception cause: ${e.cause}")
        Log.e(tag, "Stack trace:")
        e.printStackTrace()
        Log.e(tag, "=== End Detailed Error Log ===")
    }
}
