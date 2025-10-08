package com.komputerkit.socialmedia.models

data class PreloadedImage(
    val id: String = "",
    val url: String = "",
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "", // "story", "post", "background"
    val aspectRatio: String = "", // "9:16", "1:1"
    val tags: List<String> = emptyList(),
    val color: String? = null, // For background colors
    val name: String? = null, // For background colors
    val isActive: Boolean = true,
    val createdAt: Long = 0L
)

data class ImageCategory(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val description: String = "",
    val isActive: Boolean = true
)

data class StoryTemplate(
    val id: String = "",
    val name: String = "",
    val type: String = "", // "text", "image"
    val backgroundColor: String = "",
    val textColor: String = "",
    val fontSize: Int = 20,
    val fontWeight: String = "normal",
    val textAlign: String = "center",
    val template: String = "",
    val category: String = "",
    val isActive: Boolean = true
)