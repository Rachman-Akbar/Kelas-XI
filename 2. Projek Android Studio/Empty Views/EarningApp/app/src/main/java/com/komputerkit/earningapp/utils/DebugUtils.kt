package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object DebugUtils {
    private const val TAG = "DebugUtils"
    
    fun checkFirebaseStatus(context: Context? = null) {
        try {
            Log.d(TAG, "=== Firebase Status Check ===")
            
            // Check Firebase App
            if (context != null) {
                val apps = FirebaseApp.getApps(context)
                Log.d(TAG, "Firebase Apps initialized: ${apps.size}")
                if (apps.isNotEmpty()) {
                    Log.d(TAG, "Main app name: ${apps[0].name}")
                }
            }
            
            // Check Firebase Auth
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth available: ${auth != null}")
            Log.d(TAG, "Current user: ${auth.currentUser?.email ?: "None"}")
            
            // Check Firebase Firestore
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase Firestore available: ${firestore != null}")
            
            // Check network if context available
            if (context != null) {
                val networkAvailable = NetworkUtils.isNetworkAvailable(context)
                val networkType = NetworkUtils.getNetworkType(context)
                Log.d(TAG, "Network available: $networkAvailable, Type: $networkType")
            }
            
            Log.d(TAG, "=== Firebase Status Check Complete ===")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Firebase status", e)
        }
    }
    
    fun testFirebaseConnection() {
        try {
            Log.d(TAG, "=== Testing Firebase Connection ===")
            
            // Test Firebase Auth instance
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth instance: ${auth}")
            Log.d(TAG, "Firebase Auth app: ${auth.app}")
            Log.d(TAG, "Firebase Auth app name: ${auth.app.name}")
            
            // Test Firebase Firestore instance  
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "Firebase Firestore instance: ${firestore}")
            Log.d(TAG, "Firebase Firestore app: ${firestore.app}")
            
            // Test app configuration
            val app = FirebaseApp.getInstance()
            Log.d(TAG, "Firebase App instance: ${app}")
            Log.d(TAG, "Firebase App name: ${app.name}")
            Log.d(TAG, "Firebase App options: ${app.options}")
            
            Log.d(TAG, "=== Firebase Connection Test Complete ===")
        } catch (e: Exception) {
            Log.e(TAG, "Error testing Firebase connection", e)
        }
    }
    
    fun logRegistrationAttempt(email: String, name: String) {
        Log.d(TAG, "=== Registration Attempt ===")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "Name: $name")
        Log.d(TAG, "Timestamp: ${System.currentTimeMillis()}")
        checkFirebaseStatus()
        testFirebaseConnection()
        Log.d(TAG, "=== Registration Attempt Logged ===")
    }
}
