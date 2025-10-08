package com.komputerkit.easybot.data

data class Message(
    val text: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageRole {
    USER,
    MODEL,
    TYPING
}