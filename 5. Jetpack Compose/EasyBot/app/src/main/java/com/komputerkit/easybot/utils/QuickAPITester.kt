package com.komputerkit.easybot.utils

import android.util.Log
import com.komputerkit.easybot.config.OpenAIConfig
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * Quick API Key Tester
 * Panggil dari MainActivity.onCreate() untuk test API key saat app dimulai
 */
object QuickAPITester {
    
    private const val TAG = "QuickAPITester"
    
    fun testAPIKeyOnStartup() {
        // Skip jika API key masih placeholder
        if (OpenAIConfig.API_KEY == "MASUKKAN_API_KEY_ANDA_DISINI" || 
            OpenAIConfig.API_KEY.isBlank()) {
            Log.e(TAG, "❌ API KEY BELUM DIKONFIGURASI!")
            Log.e(TAG, "📝 Buka OpenAIConfig.kt dan masukkan API key yang valid")
            Log.e(TAG, "🔗 Dapatkan di: https://platform.openai.com/api-keys")
            return
        }
        
        Log.d(TAG, "🔍 Testing API Key...")
        
        runBlocking {
            try {
                val client = OkHttpClient.Builder()
                    .build()
                    
                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user") 
                            put("content", "Test")
                        })
                    })
                    put("max_tokens", 5)
                }
                
                val body = json.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .post(body)
                    .addHeader("Authorization", "Bearer ${OpenAIConfig.API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                    
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        when (response.code) {
                            200 -> {
                                Log.d(TAG, "✅ API KEY VALID - Bot siap digunakan!")
                            }
                            401 -> {
                                Log.e(TAG, "❌ API KEY TIDAK VALID atau EXPIRED")
                                Log.e(TAG, "💡 Generate API key baru di OpenAI dashboard")
                            }
                            429 -> {
                                Log.w(TAG, "⚠️ Rate limit - tapi API key valid")
                            }
                            else -> {
                                Log.e(TAG, "❌ Error: ${response.code}")
                            }
                        }
                        response.close()
                    }
                    
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "❌ Network error: ${e.message}")
                        Log.w(TAG, "🌐 Periksa koneksi internet")
                    }
                })
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Test gagal: ${e.message}")
            }
        }
    }
}