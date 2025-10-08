package com.komputerkit.aplikasimonitoringapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.komputerkit.aplikasimonitoringapp.data.model.User

/**
 * Session Manager untuk menyimpan dan mengelola token & user data
 * Menggunakan SharedPreferences
 */
class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "MonitoringAppPrefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_DATA = "user_data"
    }
    
    /**
     * Simpan token authentication
     */
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }
    
    /**
     * Ambil token authentication
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    /**
     * Simpan data user
     */
    fun saveUserData(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER_DATA, userJson).apply()
    }
    
    /**
     * Ambil data user
     */
    fun getUserData(): User? {
        val userJson = prefs.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }
    
    /**
     * Cek apakah user sudah login
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    /**
     * Clear semua session (logout)
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
