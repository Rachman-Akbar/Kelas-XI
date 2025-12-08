package com.komputerkit.aplikasimonitoringkelas.data.repository

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringkelas.data.models.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginResult = MutableStateFlow<LoginResponse?>(null)
    val loginResult: StateFlow<LoginResponse?> = _loginResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Email dan password harus diisi"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Show loading with progress
            _error.value = "Menghubungkan ke server..."

            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { loginResponse ->
                    _loginResult.value = loginResponse
                    _error.value = null
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _error.value = getErrorMessage(exception)
                    _loginResult.value = null
                    _isLoading.value = false
                }
            )
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            // Specific network exceptions
            exception is java.net.SocketTimeoutException -> {
                "Koneksi timeout. Server mungkin sibuk atau jaringan lambat. Periksa koneksi Anda."
            }
            exception is java.net.UnknownHostException -> {
                "Tidak dapat menemukan server. Periksa pengaturan jaringan dan IP server Anda."
            }
            exception is java.net.ConnectException -> {
                "Tidak dapat terhubung ke server. Pastikan server berjalan dan jaringan stabil."
            }
            exception is java.io.IOException -> {
                "Terjadi kesalahan jaringan. Periksa koneksi internet Anda."
            }
            // Specific error messages from the API
            else -> {
                val message = exception.message
                when {
                    message?.contains("timeout", ignoreCase = true) == true ->
                        "Koneksi timeout. Server mungkin sibuk atau jaringan lambat."
                    message?.contains("network", ignoreCase = true) == true ->
                        "Terjadi masalah jaringan. Periksa koneksi internet Anda."
                    message?.contains("failed to connect", ignoreCase = true) == true ->
                        "Gagal terhubung ke server. Periksa koneksi dan pengaturan server Anda."
                    message?.contains("authentication", ignoreCase = true) == true ->
                        "Autentikasi gagal. Silakan coba login kembali."
                    message?.contains("401", ignoreCase = true) == true ->
                        "Email atau password salah."
                    message?.contains("403", ignoreCase = true) == true ->
                        "Akses ditolak. Akun Anda mungkin tidak aktif atau tidak diizinkan."
                    message?.contains("422", ignoreCase = true) == true ->
                        "Data tidak valid. Periksa kembali email dan password Anda."
                    else -> {
                        // Provide user-friendly error message
                        when {
                            message.isNullOrBlank() -> "Gagal login. Silakan coba lagi."
                            else -> message
                        }
                    }
                }
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearLoginResult() {
        _loginResult.value = null
    }
}

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val repository = AuthRepository(context)
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}