package com.komputerkit.earningapp.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Global exception handler to prevent app crashes
 */
class CrashHandler private constructor(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "CrashHandler"
        
        fun initialize(application: Application) {
            try {
                val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
                val crashHandler = CrashHandler(application.applicationContext, defaultHandler)
                Thread.setDefaultUncaughtExceptionHandler(crashHandler)
                Log.d(TAG, "CrashHandler initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize CrashHandler", e)
            }
        }
    }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            Log.e(TAG, "Uncaught exception occurred", exception)
            
            // Get stack trace as string
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            exception.printStackTrace(printWriter)
            val stackTrace = stringWriter.toString()
            
            Log.e(TAG, "Stack trace: $stackTrace")
            
            // Save crash info to shared preferences for debugging
            saveCrashInfo(exception, stackTrace)
            
            // Try to restart the app gracefully
            restartApp()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in crash handler", e)
            // If our crash handler fails, use the default handler
            defaultHandler?.uncaughtException(thread, exception)
        }
    }
    
    private fun saveCrashInfo(exception: Throwable, stackTrace: String) {
        try {
            val sharedPrefs = context.getSharedPreferences("crash_info", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            
            editor.putString("last_crash_message", exception.message ?: "Unknown error")
            editor.putString("last_crash_stack", stackTrace)
            editor.putLong("last_crash_time", System.currentTimeMillis())
            editor.putString("last_crash_thread", Thread.currentThread().name)
            
            editor.apply()
            Log.d(TAG, "Crash info saved to SharedPreferences")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash info", e)
        }
    }
    
    private fun restartApp() {
        try {
            // Get the package manager and launch intent
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            
            if (intent != null) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                Log.d(TAG, "App restart initiated")
            } else {
                Log.e(TAG, "Could not get launch intent for app restart")
            }
            
            // Kill the current process
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restart app", e)
            // If restart fails, just exit
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }
    
    /**
     * Get last crash information for debugging
     */
    fun getLastCrashInfo(context: Context): CrashInfo? {
        return try {
            val sharedPrefs = context.getSharedPreferences("crash_info", Context.MODE_PRIVATE)
            
            val message = sharedPrefs.getString("last_crash_message", null)
            val stackTrace = sharedPrefs.getString("last_crash_stack", null)
            val time = sharedPrefs.getLong("last_crash_time", 0)
            val thread = sharedPrefs.getString("last_crash_thread", null)
            
            if (message != null && time > 0) {
                CrashInfo(message, stackTrace, time, thread)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get crash info", e)
            null
        }
    }
    
    /**
     * Clear crash information
     */
    fun clearCrashInfo(context: Context) {
        try {
            val sharedPrefs = context.getSharedPreferences("crash_info", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()
            Log.d(TAG, "Crash info cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear crash info", e)
        }
    }
    
    data class CrashInfo(
        val message: String,
        val stackTrace: String?,
        val timestamp: Long,
        val thread: String?
    )
}
