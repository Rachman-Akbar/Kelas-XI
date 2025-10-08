package com.komputerkit.easyshop.utils

import com.google.firebase.FirebaseException

object FirebaseErrorHandler {
    
    fun getReadableErrorMessage(exception: Exception): String {
        return when {
            exception is FirebaseException -> {
                when (exception.message) {
                    "The email address is already in use by another account." -> 
                        "Email sudah digunakan oleh akun lain"
                    "The password is invalid or the user does not have a password." -> 
                        "Password salah"
                    "There is no user record corresponding to this identifier. The user may have been deleted." -> 
                        "Email tidak terdaftar"
                    "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> 
                        "Terjadi kesalahan jaringan. Periksa koneksi internet Anda"
                    "The email address is badly formatted." -> 
                        "Format email tidak valid"
                    "The given password is invalid. [ Password should be at least 6 characters ]" -> 
                        "Password minimal 6 karakter"
                    "We have blocked all requests from this device due to unusual activity. Try again later." ->
                        "Terlalu banyak percobaan login. Coba lagi nanti"
                    else -> exception.message ?: "Terjadi kesalahan yang tidak diketahui"
                }
            }
            exception.message?.contains("network", ignoreCase = true) == true -> 
                "Terjadi kesalahan jaringan. Periksa koneksi internet Anda"
            else -> exception.message ?: "Terjadi kesalahan yang tidak diketahui"
        }
    }
}