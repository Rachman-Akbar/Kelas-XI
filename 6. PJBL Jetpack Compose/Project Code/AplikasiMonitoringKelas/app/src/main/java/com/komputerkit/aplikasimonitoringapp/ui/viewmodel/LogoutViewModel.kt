package com.komputerkit.aplikasimonitoringapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.komputerkit.aplikasimonitoringapp.data.local.AppDatabase
import com.komputerkit.aplikasimonitoringapp.data.preferences.SessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola Logout
 */
class LogoutViewModel : ViewModel() {
    
    fun logout(
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Clear session (token & user data)
                val sessionManager = SessionManager(context)
                sessionManager.clearSession()
                
                // Clear local database
                val database = AppDatabase.getDatabase(context)
                database.clearAllTables()
                
                // Callback success
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                // Even if there's error, still logout
                onSuccess()
            }
        }
    }
}
