package com.komputerkit.earningapp.data.model

data class SubjectProgress(
    val subjectId: String = "",
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val easyCompleted: Int = 0,
    val easyTotal: Int = 0,
    val mediumCompleted: Int = 0,
    val mediumTotal: Int = 0,
    val hardCompleted: Int = 0,
    val hardTotal: Int = 0,
    val totalScore: Int = 0,
    val bestStreak: Int = 0,
    val lastPlayed: Long = 0L,
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val difficultyProgress: Map<String, DifficultyProgress> = emptyMap()
) {
    fun getProgressPercentage(difficulty: String): Int {
        return when (difficulty) {
            "easy" -> if (easyTotal > 0) (easyCompleted * 100) / easyTotal else 0
            "medium" -> if (mediumTotal > 0) (mediumCompleted * 100) / mediumTotal else 0
            "hard" -> if (hardTotal > 0) (hardCompleted * 100) / hardTotal else 0
            else -> 0
        }
    }
    
    fun getTotalProgress(): Int {
        val total = easyTotal + mediumTotal + hardTotal
        val completed = easyCompleted + mediumCompleted + hardCompleted
        return if (total > 0) (completed * 100) / total else 0
    }
}

data class DifficultyProgress(
    val difficulty: String = "",
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val averageTime: Double = 0.0,
    val streak: Int = 0,
    val maxStreak: Int = 0
)
