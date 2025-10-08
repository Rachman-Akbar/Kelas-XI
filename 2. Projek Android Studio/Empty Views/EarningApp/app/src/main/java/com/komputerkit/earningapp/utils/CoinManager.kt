package com.komputerkit.earningapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Centralized coin management system
 * Handles coin loading, saving, and synchronization between Firebase and SharedPreferences
 */
class CoinManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "CoinManager"
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_COINS = "coins"
        private const val DEFAULT_COINS = 100
        
        @Volatile
        private var INSTANCE: CoinManager? = null
        
        fun getInstance(context: Context): CoinManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CoinManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val sharedPrefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Load coin balance from Firebase (priority) or SharedPreferences (fallback)
     */
    suspend fun loadCoins(): Int {
        return try {
            val user = auth.currentUser
            if (user != null) {
                // Try to load from Firebase first
                try {
                    val userDoc = firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .await()
                    
                    val firebaseCoins = userDoc.getLong("coins")?.toInt() ?: DEFAULT_COINS
                    
                    // Sync with SharedPreferences
                    sharedPrefs.edit().putInt(KEY_COINS, firebaseCoins).apply()
                    
                    Log.d(TAG, "Coins loaded from Firebase: $firebaseCoins")
                    firebaseCoins
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to load from Firebase, using SharedPreferences", e)
                    val localCoins = sharedPrefs.getInt(KEY_COINS, DEFAULT_COINS)
                    Log.d(TAG, "Coins loaded from SharedPreferences: $localCoins")
                    localCoins
                }
            } else {
                // Load from SharedPreferences only
                val localCoins = sharedPrefs.getInt(KEY_COINS, DEFAULT_COINS)
                Log.d(TAG, "Coins loaded from SharedPreferences (no user): $localCoins")
                localCoins
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading coins, using default", e)
            DEFAULT_COINS
        }
    }
    
    /**
     * Add coins to user's balance and save to both Firebase and SharedPreferences
     */
    suspend fun addCoins(amount: Int): Int {
        return try {
            val currentCoins = loadCoins()
            val newCoins = currentCoins + amount
            
            saveCoins(newCoins)
            
            Log.d(TAG, "Added $amount coins. New balance: $newCoins")
            newCoins
        } catch (e: Exception) {
            Log.e(TAG, "Error adding coins", e)
            throw e
        }
    }
    
    /**
     * Set coin balance and save to both Firebase and SharedPreferences
     */
    suspend fun saveCoins(amount: Int): Boolean {
        return try {
            // Save to SharedPreferences first (always works)
            sharedPrefs.edit().putInt(KEY_COINS, amount).apply()
            
            // Try to save to Firebase if user is logged in
            val user = auth.currentUser
            if (user != null) {
                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .update("coins", amount.toLong())
                        .await()
                    Log.d(TAG, "Coins saved to Firebase: $amount")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to save to Firebase, saved locally only", e)
                    // Still return true since we saved locally
                }
            } else {
                Log.d(TAG, "Coins saved to SharedPreferences only (no user): $amount")
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving coins", e)
            false
        }
    }
    
    /**
     * Subtract coins from user's balance
     */
    suspend fun subtractCoins(amount: Int): Int {
        val currentCoins = loadCoins()
        val newCoins = maxOf(0, currentCoins - amount) // Don't allow negative coins
        saveCoins(newCoins)
        
        Log.d(TAG, "Subtracted $amount coins. New balance: $newCoins")
        return newCoins
    }
    
    /**
     * Initialize user coins in Firebase if they don't exist
     */
    suspend fun initializeUserCoins(userId: String): Boolean {
        return try {
            val userDoc = firestore.collection("users").document(userId)
            val snapshot = userDoc.get().await()
            
            if (!snapshot.exists() || !snapshot.contains("coins")) {
                // Initialize with default coins or current SharedPreferences value
                val initialCoins = sharedPrefs.getInt(KEY_COINS, DEFAULT_COINS)
                
                val userData = mapOf(
                    "coins" to initialCoins.toLong(),
                    "lastUpdated" to System.currentTimeMillis()
                )
                
                userDoc.set(userData, com.google.firebase.firestore.SetOptions.merge()).await()
                Log.d(TAG, "User coins initialized in Firebase: $initialCoins")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing user coins", e)
            false
        }
    }
    
    /**
     * Sync coins between Firebase and SharedPreferences
     * Useful for resolving conflicts when coming back online
     */
    suspend fun syncCoins(): Int {
        return try {
            val user = auth.currentUser
            if (user != null) {
                val localCoins = sharedPrefs.getInt(KEY_COINS, DEFAULT_COINS)
                
                try {
                    val userDoc = firestore.collection("users")
                        .document(user.uid)
                        .get()
                        .await()
                    
                    val firebaseCoins = userDoc.getLong("coins")?.toInt() ?: DEFAULT_COINS
                    
                    // Use the higher value (user keeps more coins)
                    val syncedCoins = maxOf(localCoins, firebaseCoins)
                    
                    if (syncedCoins != firebaseCoins) {
                        // Update Firebase with the higher value
                        firestore.collection("users")
                            .document(user.uid)
                            .update("coins", syncedCoins.toLong())
                            .await()
                    }
                    
                    if (syncedCoins != localCoins) {
                        // Update SharedPreferences with the higher value
                        sharedPrefs.edit().putInt(KEY_COINS, syncedCoins).apply()
                    }
                    
                    Log.d(TAG, "Coins synced: $syncedCoins")
                    syncedCoins
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to sync with Firebase, using local coins", e)
                    localCoins
                }
            } else {
                val localCoins = sharedPrefs.getInt(KEY_COINS, DEFAULT_COINS)
                Log.d(TAG, "No user logged in, using local coins: $localCoins")
                localCoins
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing coins", e)
            DEFAULT_COINS
        }
    }
}
