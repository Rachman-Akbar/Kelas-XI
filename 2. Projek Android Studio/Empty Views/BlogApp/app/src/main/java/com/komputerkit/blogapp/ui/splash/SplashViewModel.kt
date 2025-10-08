package com.komputerkit.blogapp.ui.splash

import androidx.lifecycle.ViewModel
import com.komputerkit.blogapp.service.AuthService

class SplashViewModel : ViewModel() {
    
    private val authService = AuthService()
    
    fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn
    }
}