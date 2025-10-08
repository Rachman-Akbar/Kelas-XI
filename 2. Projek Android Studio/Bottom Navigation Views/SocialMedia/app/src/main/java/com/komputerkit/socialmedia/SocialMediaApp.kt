package com.komputerkit.socialmedia

import android.app.Application
import com.google.firebase.FirebaseApp
import android.util.Log
import com.komputerkit.socialmedia.utils.CrashLogger

class SocialMediaApp : Application() {
    
    companion object {
        private const val TAG = "SocialMediaApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            CrashLogger.logInfo(this, "Application onCreate started")
            
            Log.d(TAG, "Initializing Firebase...")
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
            
            CrashLogger.logInfo(this, "Firebase initialized successfully")
            CrashLogger.logInfo(this, "Application onCreate completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
            CrashLogger.logError(this, "Failed to initialize Firebase in Application", e)
            e.printStackTrace()
        }
        
        // Set up global exception handler
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            CrashLogger.logError(this, "Uncaught exception in thread: ${thread.name}", exception)
            Log.e(TAG, "Uncaught exception", exception)
        }
    }
}