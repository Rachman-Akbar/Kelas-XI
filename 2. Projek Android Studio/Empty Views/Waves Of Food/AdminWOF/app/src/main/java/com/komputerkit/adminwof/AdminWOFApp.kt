package com.komputerkit.adminwof

import android.app.Application
import com.komputerkit.adminwof.data.repository.AdminRepository
import com.komputerkit.adminwof.data.repository.FirebaseAuthRepository
import com.komputerkit.adminwof.viewmodel.ViewModelFactory

/**
 * Application class untuk initialize repositories dan ViewModelFactory
 * Menggunakan AdminRepository tunggal untuk semua CRUD operations
 */
class AdminWOFApp : Application() {
    
    // Repositories (lazy initialization)
    val authRepository by lazy { FirebaseAuthRepository() }
    val adminRepository by lazy { AdminRepository() }
    
    // ViewModelFactory
    val viewModelFactory by lazy {
        ViewModelFactory(
            authRepository = authRepository,
            adminRepository = adminRepository
        )
    }
}
