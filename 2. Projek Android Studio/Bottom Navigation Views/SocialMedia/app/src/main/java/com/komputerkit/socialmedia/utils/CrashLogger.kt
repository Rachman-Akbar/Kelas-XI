package com.komputerkit.socialmedia.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CrashLogger {
    private const val TAG = "SocialMediaCrash"
    private const val LOG_FILE = "crash_log.txt"
    
    fun logError(context: Context, error: String, exception: Throwable? = null) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] ERROR: $error"
        
        // Log to Android Log
        Log.e(TAG, logMessage, exception)
        
        // Also log to file for easier debugging
        try {
            val logFile = File(context.filesDir, LOG_FILE)
            val writer = FileWriter(logFile, true)
            writer.append("$logMessage\n")
            if (exception != null) {
                writer.append("Exception: ${exception.message}\n")
                writer.append("Stack trace: ${Log.getStackTraceString(exception)}\n")
            }
            writer.append("---\n")
            writer.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    fun logInfo(context: Context, info: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] INFO: $info"
        
        Log.i(TAG, logMessage)
        
        try {
            val logFile = File(context.filesDir, LOG_FILE)
            val writer = FileWriter(logFile, true)
            writer.append("$logMessage\n")
            writer.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write to log file", e)
        }
    }
    
    fun clearLogs(context: Context) {
        try {
            val logFile = File(context.filesDir, LOG_FILE)
            if (logFile.exists()) {
                logFile.delete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear log file", e)
        }
    }
    
    fun readLogs(context: Context): String {
        return try {
            val logFile = File(context.filesDir, LOG_FILE)
            if (logFile.exists()) {
                logFile.readText()
            } else {
                "No logs found"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read log file", e)
            "Failed to read logs: ${e.message}"
        }
    }
}