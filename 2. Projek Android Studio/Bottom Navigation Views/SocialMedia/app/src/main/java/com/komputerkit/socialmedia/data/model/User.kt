package com.komputerkit.socialmedia.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val profileImageBase64: String = "", // For Base64 support
    val bio: String = "",
    val followersCount: Long = 0,
    val followingCount: Long = 0,
    val postsCount: Long = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val isPrivate: Boolean = false
) {
    // Empty constructor for Firestore
    constructor() : this("", "", "", "", "", "", 0, 0, 0, Timestamp.now(), false)
}