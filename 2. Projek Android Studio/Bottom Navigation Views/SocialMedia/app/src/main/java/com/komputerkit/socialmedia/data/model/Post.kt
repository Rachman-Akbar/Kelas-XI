package com.komputerkit.socialmedia.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImageUrl: String = "",
    val userProfileImageBase64: String = "", // For Base64 support
    val profileImageUrl: String = "", // Alternative name for compatibility
    val imageUrl: String = "",
    val imageBase64: String = "", // For Base64 support
    val caption: String = "",
    val likesCount: Long = 0,
    val commentsCount: Long = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val likedBy: List<String> = emptyList(),
    val likes: MutableList<String> = mutableListOf(), // For compatibility
    val comments: MutableList<String> = mutableListOf(), // For compatibility
    val isLiked: Boolean = false, // For local state
    val timestamp: Long = System.currentTimeMillis(), // For compatibility
    val isVisible: Boolean = true
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "", "", "", "", "", 0, 0, Timestamp.now(), emptyList(), mutableListOf(), mutableListOf(), false, System.currentTimeMillis(), true)
}