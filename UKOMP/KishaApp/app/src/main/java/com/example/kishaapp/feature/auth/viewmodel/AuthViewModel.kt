package com.example.kishaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kishaapp.data.model.UserProfile
import com.example.kishaapp.data.model.UserRoles
import com.example.kishaapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userProfile: UserProfile? = null,
    val isGoogleOnlyAccount: Boolean = false,
    val requiresEmailVerification: Boolean = false,
    val pendingVerificationEmail: String? = null,
    val pendingVerificationPassword: String? = null,
    val canEnterApp: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshProfile()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.login(email, password)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            requiresEmailVerification = false,
                            pendingVerificationEmail = null,
                            pendingVerificationPassword = null,
                            canEnterApp = false,
                            successMessage = "Login berhasil"
                        )
                    }
                    refreshProfile()
                }
                .onFailure { e ->
                    if (e is AuthRepository.EmailNotVerifiedException) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                requiresEmailVerification = true,
                                pendingVerificationEmail = email.trim(),
                                pendingVerificationPassword = password,
                                canEnterApp = false,
                                errorMessage = e.message ?: "Email belum diverifikasi"
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Login gagal") }
                    }
                }
        }
    }

    fun resendEmailVerification(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.resendEmailVerification(email, password)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Link verifikasi telah dikirim. Cek Inbox/Spam/Promotions."
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal kirim verifikasi") }
                }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            val safeRole = if (role in listOf(UserRoles.ADMIN, UserRoles.SELLER, UserRoles.CUSTOMER)) {
                role
            } else {
                UserRoles.CUSTOMER
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.register(name, email, password, safeRole)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = false,
                            userProfile = null,
                            requiresEmailVerification = true,
                            pendingVerificationEmail = email.trim(),
                            pendingVerificationPassword = password,
                            canEnterApp = false,
                            successMessage = "Register berhasil. Link verifikasi telah dikirim ke email Anda. Verifikasi dulu sebelum login."
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Register gagal") }
                }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.sendPasswordReset(email)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Jika email terdaftar, link reset telah dikirim. Cek Inbox/Spam/Promotions."
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal kirim reset") }
                }
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoggedIn = profile != null,
                            userProfile = profile,
                            isGoogleOnlyAccount = if (profile != null) authRepository.isCurrentUserGoogleOnly() else false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Gagal memuat profile") }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.signInWithGoogle(idToken)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(isLoading = false, isLoggedIn = true, successMessage = "Login dengan Google berhasil")
                    }
                    refreshProfile()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Login Google gagal") }
                }
        }
    }

    fun setupPasswordForGoogleAccount(password: String) {
        if (password.length < 6) {
            setErrorMessage("Password minimal 6 karakter")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.createPasswordForCurrentGoogleUser(password)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Password berhasil dibuat. Sekarang akun Anda bisa login dengan email/password.",
                            isGoogleOnlyAccount = false
                        )
                    }
                    refreshProfile()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal membuat password") }
                }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun checkPendingEmailVerification() {
        val email = _uiState.value.pendingVerificationEmail
        val password = _uiState.value.pendingVerificationPassword

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Session verifikasi tidak ditemukan. Kembali ke halaman login.",
                    successMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            authRepository.checkEmailVerification(email, password)
                .onSuccess { verified ->
                    if (verified) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                canEnterApp = true,
                                successMessage = "Email sudah terverifikasi. Anda bisa masuk ke dalam aplikasi."
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                canEnterApp = false,
                                errorMessage = "Email belum terverifikasi. Cek inbox Gmail Anda lalu coba lagi."
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Gagal memeriksa verifikasi") }
                }
        }
    }

    fun resendPendingEmailVerification() {
        val email = _uiState.value.pendingVerificationEmail
        val password = _uiState.value.pendingVerificationPassword

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Session verifikasi tidak ditemukan. Kembali ke halaman login.",
                    successMessage = null
                )
            }
            return
        }

        resendEmailVerification(email, password)
    }

    fun loginFromPendingVerification() {
        val email = _uiState.value.pendingVerificationEmail
        val password = _uiState.value.pendingVerificationPassword

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "Session verifikasi tidak ditemukan. Kembali ke halaman login.",
                    successMessage = null
                )
            }
            return
        }

        login(email, password)
    }

    fun cancelPendingVerification() {
        authRepository.logout()
        _uiState.update {
            it.copy(
                requiresEmailVerification = false,
                pendingVerificationEmail = null,
                pendingVerificationPassword = null,
                canEnterApp = false,
                errorMessage = null,
                successMessage = null
            )
        }
    }

    fun setErrorMessage(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message, successMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
