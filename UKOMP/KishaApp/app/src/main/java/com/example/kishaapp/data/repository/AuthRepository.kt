package com.example.kishaapp.data.repository

import com.example.kishaapp.data.model.UserProfile
import com.example.kishaapp.data.model.UserRoles
import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseProvider.auth,
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore
) {

    class EmailNotVerifiedException(message: String) : IllegalStateException(message)

    private companion object {
        const val PROVIDER_GOOGLE = "google.com"
        const val PROVIDER_PASSWORD = "password"
        const val PROFILE_PROVIDER_GOOGLE = "google"
        const val PROFILE_PROVIDER_PASSWORD = "password"
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    fun isCurrentUserGoogleOnly(): Boolean {
        val providers = auth.currentUser?.providerData
            ?.map { it.providerId }
            ?.filter { it.isNotBlank() }
            .orEmpty()

        val hasGoogle = PROVIDER_GOOGLE in providers
        val hasPassword = PROVIDER_PASSWORD in providers
        return hasGoogle && !hasPassword
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return runCatching {
            val normalizedEmail = normalizeEmail(email)

            auth.signInWithEmailAndPassword(normalizedEmail, password).await()

            val user = auth.currentUser ?: error("User tidak ditemukan")
            user.reload().await()

            if (!user.isEmailVerified) {
                user.sendEmailVerification().await()
                auth.signOut()
                throw EmailNotVerifiedException(
                    "Email belum diverifikasi. Link verifikasi telah dikirim ulang ke email Anda."
                )
            }
            Unit
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_INVALID_EMAIL" -> throw IllegalArgumentException("Format email tidak valid")
                        "ERROR_USER_NOT_FOUND" -> throw IllegalArgumentException("Email belum terdaftar")
                        "ERROR_WRONG_PASSWORD" -> throw IllegalArgumentException("Password salah")
                        "ERROR_TOO_MANY_REQUESTS" -> throw IllegalStateException("Terlalu banyak percobaan. Coba beberapa saat lagi")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Login gagal")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun resendEmailVerification(email: String, password: String): Result<Unit> {
        return runCatching {
            val normalizedEmail = normalizeEmail(email)

            auth.signInWithEmailAndPassword(normalizedEmail, password).await()
            val user = auth.currentUser ?: error("User tidak ditemukan")
            user.reload().await()

            if (user.isEmailVerified) {
                auth.signOut()
                throw IllegalStateException("Email sudah terverifikasi. Silakan login.")
            }

            user.sendEmailVerification().await()
            auth.signOut()
            Unit
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_INVALID_EMAIL" -> throw IllegalArgumentException("Format email tidak valid")
                        "ERROR_USER_NOT_FOUND" -> throw IllegalArgumentException("Email belum terdaftar")
                        "ERROR_WRONG_PASSWORD" -> throw IllegalArgumentException("Password salah")
                        "ERROR_TOO_MANY_REQUESTS" -> throw IllegalStateException("Terlalu banyak percobaan. Coba beberapa saat lagi")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Gagal kirim verifikasi")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String = UserRoles.CUSTOMER
    ): Result<Unit> {
        return runCatching {
            val normalizedEmail = normalizeEmail(email)

            val methods = auth.fetchSignInMethodsForEmail(normalizedEmail).await().signInMethods.orEmpty()

            if (GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD in methods && EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD !in methods) {
                throw IllegalStateException(
                    "Email ini sudah terdaftar dengan Google. Login dengan Google lalu buat password di menu Profil."
                )
            }

            if (EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD in methods) {
                throw IllegalStateException("Email ini sudah terdaftar. Silakan login atau gunakan forgot password.")
            }

            val authResult = auth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            val uid = authResult.user?.uid ?: error("UID tidak ditemukan")
            val profile = UserProfile(
                uid = uid,
                name = name,
                email = normalizedEmail,
                role = role,
                authProvider = PROFILE_PROVIDER_PASSWORD
            )
            firestore.collection("users").document(uid).set(profile).await()

            authResult.user?.sendEmailVerification()?.await()
            auth.signOut()
            Unit
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_INVALID_EMAIL" -> throw IllegalArgumentException("Format email tidak valid")
                        "ERROR_WEAK_PASSWORD" -> throw IllegalArgumentException("Password terlalu lemah (minimal 6 karakter)")
                        "ERROR_EMAIL_ALREADY_IN_USE" -> throw IllegalStateException("Email ini sudah terdaftar")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Register gagal")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun checkEmailVerification(email: String, password: String): Result<Boolean> {
        return runCatching {
            val normalizedEmail = normalizeEmail(email)
            auth.signInWithEmailAndPassword(normalizedEmail, password).await()
            val user = auth.currentUser ?: error("User tidak ditemukan")
            user.reload().await()

            val isVerified = user.isEmailVerified
            if (!isVerified) {
                auth.signOut()
            }
            isVerified
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_INVALID_EMAIL" -> throw IllegalArgumentException("Format email tidak valid")
                        "ERROR_USER_NOT_FOUND" -> throw IllegalArgumentException("Email belum terdaftar")
                        "ERROR_WRONG_PASSWORD" -> throw IllegalArgumentException("Password salah")
                        "ERROR_TOO_MANY_REQUESTS" -> throw IllegalStateException("Terlalu banyak percobaan. Coba beberapa saat lagi")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Gagal memeriksa verifikasi email")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return runCatching {
            val rawEmail = email.trim()
            val normalizedEmail = normalizeEmail(email)

            val profileSnapshot = firestore.collection("users")
                .whereEqualTo("email", normalizedEmail)
                .limit(1)
                .get()
                .await()

            val profileProvider = when {
                !profileSnapshot.isEmpty -> profileSnapshot.documents.first().getString("authProvider")?.lowercase().orEmpty()
                rawEmail != normalizedEmail -> {
                    val rawSnapshot = firestore.collection("users")
                        .whereEqualTo("email", rawEmail)
                        .limit(1)
                        .get()
                        .await()
                    if (!rawSnapshot.isEmpty) rawSnapshot.documents.first().getString("authProvider")?.lowercase().orEmpty() else ""
                }
                else -> ""
            }
            if (profileProvider.isNotBlank()) {
                val provider = profileProvider
                if (provider == PROFILE_PROVIDER_GOOGLE) {
                    throw IllegalStateException("Akun ini terdaftar dengan Google. Gunakan tombol Login dengan Google.")
                }
            }

            val methods = auth.fetchSignInMethodsForEmail(normalizedEmail).await().signInMethods.orEmpty()
            val hasPasswordMethod = EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD in methods
            val hasGoogleMethod = GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD in methods

            // Block only when it is clearly a Google-only account.
            val isGoogleOnlyByMethods = hasGoogleMethod && !hasPasswordMethod
            val isGoogleOnlyByProfile = profileProvider == PROFILE_PROVIDER_GOOGLE && !hasPasswordMethod
            if (isGoogleOnlyByMethods || isGoogleOnlyByProfile) {
                throw IllegalStateException("Akun ini terdaftar dengan Google. Gunakan tombol Login dengan Google.")
            }

            // For password or unknown/mixed metadata cases, still send reset to avoid false negatives.
            auth.setLanguageCode("id")
            auth.sendPasswordResetEmail(normalizedEmail).await()
            Unit
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_INVALID_EMAIL" -> throw IllegalArgumentException("Format email tidak valid")
                        "ERROR_USER_NOT_FOUND" -> throw IllegalArgumentException("Email belum terdaftar")
                        "ERROR_TOO_MANY_REQUESTS" -> throw IllegalStateException("Terlalu banyak percobaan. Coba beberapa saat lagi")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Gagal mengirim email reset")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            val userId = auth.currentUser?.uid ?: error("UID tidak ditemukan")
            val userEmail = normalizeEmail(auth.currentUser?.email ?: "")
            val userName = auth.currentUser?.displayName ?: "User"
            if (auth.currentUser?.isEmailVerified == false) {
                auth.signOut()
                throw IllegalStateException("Akun Google harus memiliki email terverifikasi")
            }

            val methods = auth.fetchSignInMethodsForEmail(userEmail).await().signInMethods.orEmpty()
            val profileProvider = if (EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD in methods) {
                PROFILE_PROVIDER_PASSWORD
            } else {
                PROFILE_PROVIDER_GOOGLE
            }
            
            // Check if user profile already exists
            val existingProfile = firestore.collection("users").document(userId).get().await()
            if (!existingProfile.exists()) {
                auth.signOut()
                throw IllegalStateException("Data user tidak pernah ditemukan")
            }

            firestore.collection("users").document(userId).update(
                mapOf(
                    "email" to userEmail.lowercase(),
                    "name" to userName,
                    "authProvider" to profileProvider
                )
            ).await()
            Unit
        }
    }

    suspend fun createPasswordForCurrentGoogleUser(password: String): Result<Unit> {
        return runCatching {
            val currentUser = auth.currentUser ?: error("User belum login")
            val email = currentUser.email?.trim()?.lowercase() ?: error("Email akun tidak ditemukan")

            val methods = auth.fetchSignInMethodsForEmail(email).await().signInMethods.orEmpty()
            if (EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD in methods) {
                throw IllegalStateException("Akun ini sudah memiliki password")
            }

            if (GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD !in methods) {
                throw IllegalStateException("Fitur ini hanya untuk akun Google")
            }

            val credential = EmailAuthProvider.getCredential(email, password)
            currentUser.linkWithCredential(credential).await()

            val uid = currentUser.uid
            val existingProfile = firestore.collection("users").document(uid).get().await()
            if (existingProfile.exists()) {
                firestore.collection("users").document(uid).update("authProvider", PROFILE_PROVIDER_PASSWORD).await()
            }
            Unit
        }.recoverCatching { throwable ->
            when (throwable) {
                is FirebaseAuthRecentLoginRequiredException -> {
                    throw IllegalStateException("Sesi login sudah lama. Silakan login ulang dengan Google lalu coba lagi")
                }
                is FirebaseNetworkException -> throw IllegalStateException("Koneksi internet bermasalah. Coba lagi.")
                is FirebaseAuthException -> {
                    when (throwable.errorCode) {
                        "ERROR_WEAK_PASSWORD" -> throw IllegalArgumentException("Password terlalu lemah (minimal 6 karakter)")
                        "ERROR_EMAIL_ALREADY_IN_USE" -> throw IllegalStateException("Email sudah terpakai akun lain")
                        else -> throw IllegalStateException(throwable.localizedMessage ?: "Gagal membuat password")
                    }
                }
                else -> throw throwable
            }
        }
    }

    suspend fun getCurrentUserProfile(): Result<UserProfile?> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: return@runCatching null
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.toObject(UserProfile::class.java)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
