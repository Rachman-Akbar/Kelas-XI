package com.komputerkit.aplikasimonitoringapp.utils

/**
 * API Configuration Helper
 * 
 * Gunakan object ini untuk mengganti base URL jika diperlukan
 */
object ApiConfig {
    
    /**
     * Base URLs untuk berbagai environment
     */
    object BaseUrls {
        const val EMULATOR = "http://10.0.2.2:8000/api/"
        const val LOCALHOST = "http://127.0.0.1:8000/api/"
        
        // Ganti dengan IP komputer Anda untuk physical device
        const val PHYSICAL_DEVICE = "http://192.168.1.100:8000/api/"
        
        // Production URL (jika sudah deploy)
        const val PRODUCTION = "https://your-domain.com/api/"
    }
    
    /**
     * Default credentials untuk testing
     */
    object TestAccounts {
        const val ADMIN_EMAIL = "admin@sekolah.com"
        const val ADMIN_PASSWORD = "admin123"
        
        const val GURU_EMAIL = "budi.guru@sekolah.com"
        const val GURU_PASSWORD = "budi123"
        
        const val SISWA_EMAIL = "andi.siswa@sekolah.com"
        const val SISWA_PASSWORD = "andi123"
    }
    
    /**
     * Helper function untuk mendapatkan IP Address dari URL
     */
    fun getIpFromUrl(url: String): String {
        return url.substringAfter("//").substringBefore(":")
    }
    
    /**
     * Helper function untuk validate URL format
     */
    fun isValidUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }
}
