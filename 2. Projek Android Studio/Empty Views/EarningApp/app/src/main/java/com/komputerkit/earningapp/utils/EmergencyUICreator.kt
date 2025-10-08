package com.komputerkit.earningapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.view.Gravity
import android.graphics.Color

/**
 * Emergency fallback UI creation when layout inflation fails
 */
object EmergencyUICreator {
    
    private const val TAG = "EmergencyUICreator"
    
    /**
     * Create a basic emergency layout when normal layout fails
     */
    fun createEmergencyLayout(context: Context, title: String, message: String): LinearLayout {
        return try {
            Log.d(TAG, "Creating emergency layout for: $title")
            
            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(32, 32, 32, 32)
                setBackgroundColor(Color.WHITE)
            }
            
            // Title
            val titleView = TextView(context).apply {
                text = title
                textSize = 20f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                setPadding(16, 16, 16, 8)
            }
            layout.addView(titleView)
            
            // Message
            val messageView = TextView(context).apply {
                text = message
                textSize = 16f
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER
                setPadding(16, 8, 16, 24)
            }
            layout.addView(messageView)
            
            // Restart button
            val restartButton = Button(context).apply {
                text = "Restart App"
                setPadding(24, 12, 24, 12)
                setOnClickListener {
                    try {
                        if (context is Activity) {
                            context.finishAffinity()
                        }
                        // Force restart
                        val packageManager = context.packageManager
                        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
                        if (intent != null) {
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                        android.os.Process.killProcess(android.os.Process.myPid())
                    } catch (e: Exception) {
                        Log.e(TAG, "Error restarting app", e)
                    }
                }
            }
            layout.addView(restartButton)
            
            Log.d(TAG, "Emergency layout created successfully")
            layout
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create emergency layout", e)
            // Return a minimal layout if even emergency layout fails
            LinearLayout(context).apply {
                setBackgroundColor(Color.WHITE)
            }
        }
    }
    
    /**
     * Set emergency content view for activity
     */
    fun setEmergencyContentView(activity: Activity, title: String, message: String) {
        try {
            val emergencyLayout = createEmergencyLayout(activity, title, message)
            activity.setContentView(emergencyLayout)
            Log.d(TAG, "Emergency content view set for activity")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set emergency content view", e)
            // Last resort - try to finish the activity
            try {
                activity.finish()
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to finish activity", e2)
            }
        }
    }
}
