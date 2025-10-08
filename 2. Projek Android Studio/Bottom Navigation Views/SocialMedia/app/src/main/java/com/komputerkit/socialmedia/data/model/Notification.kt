package com.komputerkit.socialmedia.data.model

data class Notification(
    val id: String = "",
    val type: String = "", // "like", "comment", "follow"
    val fromUserId: String = "",
    val fromUsername: String = "",
    val fromUserProfileUrl: String = "",
    val message: String = "",
    val postImageUrl: String? = null,
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)