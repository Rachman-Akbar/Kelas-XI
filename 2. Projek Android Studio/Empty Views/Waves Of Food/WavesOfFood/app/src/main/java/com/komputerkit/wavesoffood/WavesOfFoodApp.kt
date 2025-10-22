package com.komputerkit.wavesoffood

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.wavesoffood.data.repository.EcommerceRepository
import com.komputerkit.wavesoffood.data.repository.FirebaseAuthRepository
import com.komputerkit.wavesoffood.viewmodel.ViewModelFactory

/**
 * Application class untuk initialize Firebase dan Repositories
 */
class WavesOfFoodApp : Application() {
    
    // Repositories (Singleton pattern)
    val authRepository by lazy {
        FirebaseAuthRepository(
            auth = FirebaseAuth.getInstance(),
            firestore = FirebaseFirestore.getInstance()
        )
    }
    
    val ecommerceRepository by lazy {
        EcommerceRepository(
            firestore = FirebaseFirestore.getInstance()
        )
    }
    
    // ViewModelFactory (Singleton)
    val viewModelFactory by lazy {
        ViewModelFactory(
            authRepository = authRepository,
            ecommerceRepository = ecommerceRepository
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
