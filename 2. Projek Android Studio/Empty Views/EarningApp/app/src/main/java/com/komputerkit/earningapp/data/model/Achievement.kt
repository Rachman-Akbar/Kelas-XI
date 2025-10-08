package com.komputerkit.earningapp.data.model

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val type: String = "", // quiz_completion, score, streak, subject_master, speed, daily_streak, subject_explorer, perfect
    val requirement: Int = 0,
    val reward: Int = 0, // coins
    val isActive: Boolean = true,
    val subjectId: String? = null, // for subject-specific achievements
    val createdAt: Long = 0
)
