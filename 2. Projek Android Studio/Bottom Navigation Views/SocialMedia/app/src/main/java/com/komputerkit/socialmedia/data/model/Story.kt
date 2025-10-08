package com.komputerkit.socialmedia.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Story(
    @DocumentId
    val storyId: String = "",
    val userId: String = "",
    val username: String = "",
    val userProfileImageUrl: String = "",
    val userProfileImageBase64: String = "", // For Base64 support
    val profileImageUrl: String = "", // Alternative name for compatibility
    val imageUrl: String = "",
    val imageBase64: String = "", // For Base64 support
    val text: String = "",
    val caption: String = "", // Alternative name for compatibility
    val backgroundColor: String = "#000000",
    val createdAt: Timestamp = Timestamp.now(),
    val expiresAt: Timestamp = Timestamp.now(),
    val viewedBy: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(), // For compatibility
    val isViewed: Boolean = false, // For local state
    val isActive: Boolean = true
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "", "", "", "", "", "", "#000000", Timestamp.now(), Timestamp.now(), emptyList(), System.currentTimeMillis(), false, true)
}