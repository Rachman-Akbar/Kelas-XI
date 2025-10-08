package com.komputerkit.whatsapp.models

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageText: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: String = "text", // text, image, video, etc.
    val isRead: Boolean = false,
    val status: String = "sending" // sending, sent, delivered, read
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", System.currentTimeMillis(), "text", false, "sending")
}