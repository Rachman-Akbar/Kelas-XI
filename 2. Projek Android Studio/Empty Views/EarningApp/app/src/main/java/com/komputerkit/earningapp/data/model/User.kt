package com.komputerkit.earningapp.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val phoneNumber: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val bio: String? = null,
    val totalScore: Int = 0,
    val level: Int = 1,
    val coins: Int = 100,
    val subjectProgress: Map<String, SubjectProgress> = emptyMap(),
    val dailyStreak: Int = 0,
    val maxDailyStreak: Int = 0,
    val lastLoginAt: Long = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val achievements: List<String> = emptyList(),
    val statistics: Statistics? = null
) {
    data class Statistics(
        val totalQuizzes: Int = 0,
        val totalTimeSpent: Int = 0, // in seconds
        val averageScore: Double = 0.0,
        val favoriteSubject: String = "",
        val strongestDifficulty: String = "easy",
        val weeklyGoal: Int = 5,
        val weeklyProgress: Int = 0
    )
    
    // Helper functions
    fun getAccuracy(): Double {
        val totalQuestions = subjectProgress.values.sumOf { it.totalQuestions }
        val correctAnswers = subjectProgress.values.sumOf { it.correctAnswers }
        return if (totalQuestions > 0) (correctAnswers.toDouble() / totalQuestions * 100) else 0.0
    }
    
    fun getProgressInLevel(): Double {
        val currentLevelMinScore = when (level) {
            1 -> 0
            2 -> 100
            3 -> 300
            4 -> 600
            5 -> 1000
            6 -> 1500
            7 -> 2100
            8 -> 2800
            9 -> 3600
            10 -> 4500
            else -> 4500
        }
        
        val nextLevelMinScore = when (level) {
            1 -> 100
            2 -> 300
            3 -> 600
            4 -> 1000
            5 -> 1500
            6 -> 2100
            7 -> 2800
            8 -> 3600
            9 -> 4500
            10 -> 5500
            else -> 5500
        }
        
        val progressInLevel = totalScore - currentLevelMinScore
        val levelRange = nextLevelMinScore - currentLevelMinScore
        
        return if (levelRange > 0) (progressInLevel.toDouble() / levelRange * 100) else 100.0
    }
    
    fun getNextLevelScore(): Int {
        return when (level) {
            1 -> 100
            2 -> 300
            3 -> 600
            4 -> 1000
            5 -> 1500
            6 -> 2100
            7 -> 2800
            8 -> 3600
            9 -> 4500
            10 -> 5500
            else -> 5500
        }
    }
}
