package com.komputerkit.earningapp.data.model

/**
 * Data class to track user's answered questions
 * This prevents the same correctly answered question from appearing again
 * until all questions in the pool are exhausted
 */
data class AnsweredQuestion(
    val id: String = "",
    val userId: String = "",
    val questionId: String = "",
    val subjectId: String = "",
    val difficulty: String = "",
    val isCorrect: Boolean = false,
    val answeredAt: Long = System.currentTimeMillis(),
    val timeSpent: Long = 0, // in milliseconds
    val selectedOption: Int = -1
)

/**
 * Data class to track question pools for each subject and difficulty
 * This helps ensure proper question rotation
 */
data class QuestionPool(
    val id: String = "",
    val userId: String = "",
    val subjectId: String = "",
    val difficulty: String = "",
    val totalQuestions: Int = 0,
    val answeredCorrectly: List<String> = emptyList(), // List of question IDs
    val lastResetAt: Long = System.currentTimeMillis(),
    val isPoolExhausted: Boolean = false
) {
    /**
     * Check if all questions in this pool have been answered correctly
     */
    fun isExhausted(): Boolean {
        return answeredCorrectly.size >= totalQuestions
    }
    
    /**
     * Get remaining questions count
     */
    fun getRemainingCount(): Int {
        return maxOf(0, totalQuestions - answeredCorrectly.size)
    }
    
    /**
     * Get progress percentage
     */
    fun getProgressPercentage(): Int {
        return if (totalQuestions > 0) {
            (answeredCorrectly.size * 100) / totalQuestions
        } else 0
    }
}
