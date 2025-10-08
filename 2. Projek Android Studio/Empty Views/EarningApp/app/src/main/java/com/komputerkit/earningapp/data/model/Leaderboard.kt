package com.komputerkit.earningapp.data.model

data class Leaderboard(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImageUrl: String = "",
    val totalScore: Int = 0,
    val level: Int = 1,
    val rank: Int = 0,
    val subjectScores: Map<String, Int> = emptyMap(),
    val lastUpdated: Long = System.currentTimeMillis()
)

data class UserAchievement(
    val id: String = "",
    val userId: String = "",
    val achievementId: String = "",
    val progress: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val claimedAt: Long? = null
)
