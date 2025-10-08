package com.komputerkit.whatsapp.models

data class Chat(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessageSender: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", emptyList(), "", System.currentTimeMillis(), "")
}