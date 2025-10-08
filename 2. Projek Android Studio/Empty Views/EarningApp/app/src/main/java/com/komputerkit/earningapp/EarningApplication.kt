package com.komputerkit.earningapp

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EarningApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            Log.d("EarningApplication", "Starting application initialization...")
            
            // Initialize crash handler first
            try {
                com.komputerkit.earningapp.utils.CrashHandler.initialize(this)
            } catch (e: Exception) {
                Log.w("EarningApplication", "Failed to initialize crash handler", e)
            }
            
            // Force vector drawables compatibility
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            
            // Initialize Firebase with enhanced error handling
            initializeFirebase()
            
            // Initialize AuthRepository context for local fallback
            com.komputerkit.earningapp.data.repository.AuthRepository.initialize(this)
            
            Log.d("EarningApplication", "Application initialized successfully")
        } catch (e: Exception) {
            Log.e("EarningApplication", "Error in Application onCreate", e)
            // Continue execution even if some initialization fails
        }
    }
    
    private fun initializeFirebase() {
        try {
            Log.d("EarningApplication", "Starting Firebase initialization...")
            
            // Check for Google Play Services availability first
            try {
                val gpsAvailable = com.google.android.gms.common.GoogleApiAvailability.getInstance()
                val resultCode = gpsAvailable.isGooglePlayServicesAvailable(this)
                if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
                    Log.w("EarningApplication", "Google Play Services not available: $resultCode")
                    // Continue with limited functionality
                }
            } catch (e: SecurityException) {
                Log.w("EarningApplication", "SecurityException checking Google Play Services", e)
                // Continue with Firebase initialization even if GPS check fails
            } catch (e: Exception) {
                Log.w("EarningApplication", "Error checking Google Play Services", e)
            }
            
            // Check if Firebase is already initialized
            val firebaseApps = FirebaseApp.getApps(this)
            if (firebaseApps.isEmpty()) {
                Log.d("EarningApplication", "Initializing Firebase...")
                
                // Try default initialization first (uses google-services.json)
                try {
                    FirebaseApp.initializeApp(this)
                    Log.d("EarningApplication", "Firebase initialized with default configuration")
                } catch (e: SecurityException) {
                    Log.w("EarningApplication", "SecurityException during Firebase initialization", e)
                    // Try to continue with manual configuration
                    tryManualFirebaseInit()
                } catch (e: IllegalStateException) {
                    Log.w("EarningApplication", "IllegalStateException during Firebase initialization", e)
                    tryManualFirebaseInit()
                } catch (e: Exception) {
                    Log.w("EarningApplication", "Default initialization failed, trying explicit configuration", e)
                    tryManualFirebaseInit()
                }
            } else {
                Log.d("EarningApplication", "Firebase already initialized")
            }
            
            // Configure Firebase services with error handling
            configureFirebaseServices()
            
        } catch (e: SecurityException) {
            Log.e("EarningApplication", "SecurityException in Firebase initialization", e)
            // Continue app execution without Firebase
        } catch (e: Exception) {
            Log.e("EarningApplication", "Complete Firebase initialization failed", e)
        }
    }
    
    private fun tryManualFirebaseInit() {
        try {
            // Fallback: Manual configuration
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:942466606409:android:26c5006250c92a06a6e698")
                .setApiKey("AIzaSyDZVGXES5-o_SjqJERM_EAOTxZP8B8BZSI")
                .setProjectId("earningapp-cd3cd")
                .setStorageBucket("earningapp-cd3cd.firebasestorage.app")
                .setGcmSenderId("942466606409")
                .build()
            
            FirebaseApp.initializeApp(this, options)
            Log.d("EarningApplication", "Firebase initialized with explicit configuration")
        } catch (e: SecurityException) {
            Log.e("EarningApplication", "SecurityException in manual Firebase initialization", e)
            // Continue without Firebase - app should still work with local data
        } catch (e: IllegalStateException) {
            Log.e("EarningApplication", "IllegalStateException in manual Firebase initialization", e)
        } catch (ex: Exception) {
            Log.e("EarningApplication", "Manual Firebase initialization also failed", ex)
            // Continue without Firebase - app should still work with local data
        }
    }
    
    private fun configureFirebaseServices() {
        try {
            // Verify and configure Firebase services
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            
            Log.d("EarningApplication", "Firebase Auth initialized: ${auth != null}")
            Log.d("EarningApplication", "Firebase Firestore initialized: ${firestore != null}")
            
            // Configure Firebase Auth for better error handling
            try {
                // Set language to Indonesian
                auth.setLanguageCode("id")
                Log.d("EarningApplication", "Firebase Auth language set to Indonesian")
            } catch (e: SecurityException) {
                Log.w("EarningApplication", "SecurityException setting Auth language", e)
            } catch (e: Exception) {
                Log.w("EarningApplication", "Could not set Auth language", e)
            }
            
            // Set Firestore settings for better offline support
            try {
                @Suppress("DEPRECATION")
                val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
                firestore.firestoreSettings = settings
                Log.d("EarningApplication", "Firestore settings configured")
            } catch (e: SecurityException) {
                Log.w("EarningApplication", "SecurityException configuring Firestore", e)
            } catch (e: Exception) {
                Log.w("EarningApplication", "Could not configure Firestore settings", e)
            }
            
        } catch (e: SecurityException) {
            Log.e("EarningApplication", "SecurityException in Firebase services configuration", e)
        } catch (e: Exception) {
            Log.e("EarningApplication", "Firebase services configuration failed", e)
        }
        
        // Data initialization removed as quiz feature has been removed
        Log.d("EarningApplication", "Quiz data initialization skipped - feature removed")
    }
}
