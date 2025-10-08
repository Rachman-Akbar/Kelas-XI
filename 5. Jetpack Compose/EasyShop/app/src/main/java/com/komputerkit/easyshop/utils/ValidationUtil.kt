package com.komputerkit.easyshop.utils

import com.komputerkit.easyshop.data.model.ValidationResult
import java.util.regex.Pattern

object ValidationUtil {
    
    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    )
    
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email tidak boleh kosong")
            !EMAIL_PATTERN.matcher(email).matches() -> ValidationResult(false, "Format email tidak valid")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password tidak boleh kosong")
            password.length < 6 -> ValidationResult(false, "Password minimal 6 karakter")
            else -> ValidationResult(true)
        }
    }
    
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Nama tidak boleh kosong")
            name.length < 2 -> ValidationResult(false, "Nama minimal 2 karakter")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult(false, "Nomor telepon tidak boleh kosong")
            phone.length < 10 -> ValidationResult(false, "Nomor telepon minimal 10 digit")
            !phone.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } -> 
                ValidationResult(false, "Nomor telepon hanya boleh berisi angka, +, -, dan spasi")
            else -> ValidationResult(true)
        }
    }
    
    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult(false, "Konfirmasi password tidak boleh kosong")
            password != confirmPassword -> ValidationResult(false, "Password tidak cocok")
            else -> ValidationResult(true)
        }
    }
}