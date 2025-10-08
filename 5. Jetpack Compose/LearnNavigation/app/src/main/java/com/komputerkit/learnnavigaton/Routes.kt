package com.komputerkit.learnnavigaton

/**
 * Object yang berisi definisi rute-rute untuk navigasi dalam aplikasi
 */
object Routes {
    const val SCREEN_A = "screen_a"
    const val SCREEN_B = "screen_b/{name}"
    
    /**
     * Fungsi untuk membuat rute Screen B dengan parameter nama
     */
    fun createScreenBRoute(name: String): String {
        return "screen_b/$name"
    }
}