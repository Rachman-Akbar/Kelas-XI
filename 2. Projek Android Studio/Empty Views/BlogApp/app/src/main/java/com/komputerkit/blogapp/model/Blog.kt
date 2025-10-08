package com.komputerkit.blogapp.model

data class Blog(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String = ""
) {
    // Constructor kosong diperlukan untuk Firestore
    constructor() : this("", "", "", "", "", 0L, "")
}