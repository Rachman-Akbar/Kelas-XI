package com.komputerkit.earningapp.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.data.model.AuthUser
import com.komputerkit.earningapp.utils.Resource
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository? = null,
    private val context: Context? = null
) : ViewModel() {

    private val _authState = MutableLiveData<Resource<AuthUser>>()
    val authState: LiveData<Resource<AuthUser>> = _authState

    private val _currentUser = MutableLiveData<AuthUser?>()
    val currentUser: LiveData<AuthUser?> = _currentUser

    private val repository: AuthRepository by lazy {
        authRepository ?: try {
            AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error creating AuthRepository, using default constructor", e)
            AuthRepository()
        }
    }
    
    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    init {
        try {
            // Convert Firebase user to AuthUser if available
            val firebaseUser = repository.getCurrentUser()
            _currentUser.value = if (firebaseUser != null) {
                AuthUser.fromFirebaseUser(firebaseUser)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error getting current user in init", e)
            _currentUser.value = null
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = Resource.Loading()
                val result = repository.signIn(email, password)
                _authState.value = result
                if (result is Resource.Success) {
                    _currentUser.value = result.data
                    // Save user info to SharedPreferences
                    saveUserToPreferences(result.data)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in signIn", e)
                _authState.value = Resource.Error("Terjadi kesalahan saat login: ${e.localizedMessage}")
            }
        }
    }

    fun signUp(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _authState.value = Resource.Loading()
                val result = repository.signUp(email, password, name)
                _authState.value = result
                if (result is Resource.Success) {
                    _currentUser.value = result.data
                    // Save user info to SharedPreferences
                    saveUserToPreferences(result.data)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in signUp", e)
                _authState.value = Resource.Error("Terjadi kesalahan saat registrasi: ${e.localizedMessage}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
                _currentUser.value = null
                // Clear user info from SharedPreferences
                clearUserFromPreferences()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in signOut", e)
            }
        }
    }
    
    private fun saveUserToPreferences(authUser: AuthUser?) {
        try {
            authUser?.let { user ->
                sharedPreferences?.edit()?.apply {
                    putString("user_uid", user.uid)
                    putString("user_email", user.email)
                    putString("user_name", user.displayName)
                    putBoolean("is_logged_in", true)
                    apply()
                }
                Log.d("AuthViewModel", "User info saved to SharedPreferences")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error saving user to preferences", e)
        }
    }
    
    private fun clearUserFromPreferences() {
        try {
            sharedPreferences?.edit()?.apply {
                remove("user_uid")
                remove("user_email") 
                remove("user_name")
                remove("profile_image_url")
                putBoolean("is_logged_in", false)
                apply()
            }
            Log.d("AuthViewModel", "User info cleared from SharedPreferences")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error clearing user from preferences", e)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = Resource.Loading()
                val result = repository.resetPassword(email)
                @Suppress("UNCHECKED_CAST")
                _authState.value = if (result is Resource.Success) {
                    Resource.Success(null) as Resource<AuthUser>
                } else {
                    Resource.Error(result.message ?: "Failed to send reset email")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in resetPassword", e)
                _authState.value = Resource.Error("Gagal mengirim email reset: ${e.localizedMessage}")
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return try {
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error validating email", e)
            false
        }
    }

    fun validatePassword(password: String): Boolean {
        return try {
            password.length >= 6
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error validating password", e)
            false
        }
    }
}
