package com.komputerkit.whatsapp.models

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profilePic: String = "",
    val status: String = "Available",
    val lastSeen: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "Available", System.currentTimeMillis())
}