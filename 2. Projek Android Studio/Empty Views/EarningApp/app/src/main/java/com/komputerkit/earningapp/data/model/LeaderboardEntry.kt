package com.komputerkit.earningapp.data.model

data class LeaderboardEntry(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val totalScore: Int = 0,
    val totalQuizzes: Int = 0,
    val avgScore: Double = 0.0,
    val level: Int = 1,
    val profileImageUrl: String = "",
    val lastUpdated: Long = 0,
    var rank: Int = 0 // This will be set when loading data
) {
    // Helper methods
    fun getDisplayText(): String {
        return displayName.ifEmpty { 
            email.substringBefore("@").ifEmpty { "User" }
        }
    }
    
    fun getFormattedScore(): String {
        return when {
            totalScore >= 1000000 -> "${(totalScore / 1000000.0).format(1)}M"
            totalScore >= 1000 -> "${(totalScore / 1000.0).format(1)}K"
            else -> totalScore.toString()
        }
    }
    
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
