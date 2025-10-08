package com.komputerkit.earningapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.earningapp.data.model.User
import com.komputerkit.earningapp.data.model.Achievement
import com.komputerkit.earningapp.data.model.Leaderboard
import com.komputerkit.earningapp.data.model.SubjectProgress
import com.komputerkit.earningapp.utils.Resource
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    // Get current user profile
    suspend fun getCurrentUserProfile(): Resource<User> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    if (user != null) {
                        Resource.Success(user.copy(id = currentUser.uid))
                    } else {
                        Resource.Error("Failed to parse user data")
                    }
                } else {
                    Resource.Error("User profile not found")
                }
            } else {
                Resource.Error("No authenticated user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    // Create user profile after registration
    suspend fun createUserProfile(user: User): Resource<User> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userProfile = user.copy(
                    id = currentUser.uid,
                    email = currentUser.email ?: user.email,
                    createdAt = System.currentTimeMillis(),
                    lastLoginAt = System.currentTimeMillis(),
                    totalScore = 0,
                    level = 1,
                    coins = 100,
                    dailyStreak = 0,
                    maxDailyStreak = 0,
                    achievements = emptyList(),
                    subjectProgress = emptyMap(),
                    statistics = User.Statistics(
                        totalQuizzes = 0,
                        totalTimeSpent = 0,
                        averageScore = 0.0,
                        favoriteSubject = "",
                        strongestDifficulty = "easy",
                        weeklyGoal = 5,
                        weeklyProgress = 0
                    )
                )
                
                firestore.collection("users")
                    .document(currentUser.uid)
                    .set(userProfile)
                    .await()
                
                // Create default user settings
                val defaultSettings = mapOf(
                    "userId" to currentUser.uid,
                    "notifications" to mapOf(
                        "dailyReminder" to true,
                        "achievementAlerts" to true,
                        "weeklyReport" to true,
                        "sound" to true
                    ),
                    "preferences" to mapOf(
                        "language" to "id",
                        "theme" to "light",
                        "autoAdvance" to false,
                        "showExplanations" to true,
                        "timeLimit" to true
                    ),
                    "privacy" to mapOf(
                        "showInLeaderboard" to true,
                        "shareProgress" to false,
                        "dataCollection" to true
                    ),
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )
                
                firestore.collection("user_settings")
                    .document(currentUser.uid)
                    .set(defaultSettings)
                    .await()
                
                Resource.Success(userProfile)
            } else {
                Resource.Error("No authenticated user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create user profile")
        }
    }
    
    // Update user login timestamp and daily streak
    suspend fun updateUserLogin(): Resource<User> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userRef = firestore.collection("users").document(currentUser.uid)
                val userDoc = userRef.get().await()
                
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    if (user != null) {
                        val now = System.currentTimeMillis()
                        val lastLogin = user.lastLoginAt
                        val timeDiff = now - lastLogin
                        
                        // Check if this is a consecutive day (within 48 hours)
                        val isConsecutiveDay = timeDiff >= 86400000 && timeDiff <= 172800000 // 24-48 hours
                        
                        var newStreak = user.dailyStreak
                        if (isConsecutiveDay) {
                            newStreak += 1
                        } else if (timeDiff > 172800000) {
                            newStreak = 1 // Reset streak if more than 48 hours
                        }
                        
                        val updates = mapOf(
                            "lastLoginAt" to now,
                            "dailyStreak" to newStreak,
                            "maxDailyStreak" to maxOf(user.maxDailyStreak, newStreak)
                        )
                        
                        userRef.update(updates).await()
                        
                        val updatedUser = user.copy(
                            lastLoginAt = now,
                            dailyStreak = newStreak,
                            maxDailyStreak = maxOf(user.maxDailyStreak, newStreak)
                        )
                        
                        Resource.Success(updatedUser)
                    } else {
                        Resource.Error("Failed to parse user data")
                    }
                } else {
                    Resource.Error("User not found")
                }
            } else {
                Resource.Error("No authenticated user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user login")
        }
    }
    
    // Update user score and progress
    suspend fun updateUserScore(additionalScore: Int, subjectId: String, difficulty: String): Resource<User> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userRef = firestore.collection("users").document(currentUser.uid)
                val userDoc = userRef.get().await()
                
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    if (user != null) {
                        val newTotalScore = user.totalScore + additionalScore
                        val newLevel = calculateLevel(newTotalScore)
                        val newCoins = user.coins + (additionalScore / 10) // 1 coin per 10 points
                        
                        // Update subject progress
                        val currentSubjectProgress = user.subjectProgress[subjectId]
                        val updatedSubjectProgress = if (currentSubjectProgress != null) {
                            currentSubjectProgress.copy(
                                totalQuestions = currentSubjectProgress.totalQuestions + 1,
                                correctAnswers = currentSubjectProgress.correctAnswers + 1,
                                lastAccessedAt = System.currentTimeMillis()
                            )
                        } else {
                            SubjectProgress(
                                subjectId = subjectId,
                                totalQuestions = 1,
                                correctAnswers = 1,
                                difficultyProgress = emptyMap(),
                                lastAccessedAt = System.currentTimeMillis()
                            )
                        }
                        
                        val updates = mapOf(
                            "totalScore" to newTotalScore,
                            "level" to newLevel,
                            "coins" to newCoins,
                            "subjectProgress.$subjectId" to updatedSubjectProgress,
                            "statistics.totalQuizzes" to (user.statistics?.totalQuizzes ?: 0) + 1,
                            "statistics.averageScore" to calculateAverageScore(user.statistics?.totalQuizzes ?: 0, user.statistics?.averageScore ?: 0.0, additionalScore)
                        )
                        
                        userRef.update(updates).await()
                        
                        val updatedUser = user.copy(
                            totalScore = newTotalScore,
                            level = newLevel,
                            coins = newCoins
                        )
                        
                        Resource.Success(updatedUser)
                    } else {
                        Resource.Error("Failed to parse user data")
                    }
                } else {
                    Resource.Error("User not found")
                }
            } else {
                Resource.Error("No authenticated user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update user score")
        }
    }
    
    // Get user achievements
    suspend fun getUserAchievements(): Resource<List<Achievement>> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userDoc = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                    val userAchievementIds = user?.achievements ?: emptyList()
                    
                    if (userAchievementIds.isNotEmpty()) {
                        val achievementsQuery = firestore.collection("achievements")
                            .whereIn("id", userAchievementIds)
                            .get()
                            .await()
                        
                        val achievements = achievementsQuery.documents.mapNotNull { doc ->
                            doc.toObject(Achievement::class.java)
                        }
                        
                        Resource.Success(achievements)
                    } else {
                        Resource.Success(emptyList())
                    }
                } else {
                    Resource.Error("User not found")
                }
            } else {
                Resource.Error("No authenticated user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user achievements")
        }
    }
    
    // Get leaderboard
    suspend fun getLeaderboard(limit: Int = 10): Resource<List<Leaderboard>> {
        return try {
            val leaderboardQuery = firestore.collection("leaderboard")
                .orderBy("totalScore", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            
            val leaderboard = leaderboardQuery.documents.mapNotNull { doc ->
                doc.toObject(Leaderboard::class.java)
            }
            
            Resource.Success(leaderboard)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get leaderboard")
        }
    }
    
    // Check and award achievements
    suspend fun checkAchievements(user: User): Resource<List<Achievement>> {
        return try {
            val allAchievementsQuery = firestore.collection("achievements")
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val allAchievements = allAchievementsQuery.documents.mapNotNull { doc ->
                doc.toObject(Achievement::class.java)
            }
            
            val newAchievements = mutableListOf<Achievement>()
            
            for (achievement in allAchievements) {
                if (!user.achievements.contains(achievement.id)) {
                    val isEarned = when (achievement.type) {
                        "quiz_completion" -> (user.statistics?.totalQuizzes ?: 0) >= achievement.requirement
                        "score" -> user.totalScore >= achievement.requirement
                        "streak" -> user.dailyStreak >= achievement.requirement
                        "daily_streak" -> user.dailyStreak >= achievement.requirement
                        "subject_master" -> {
                            val subjectProgress = user.subjectProgress[achievement.subjectId]
                            (subjectProgress?.totalQuestions ?: 0) >= achievement.requirement
                        }
                        "perfect" -> user.statistics?.averageScore ?: 0.0 >= achievement.requirement
                        "subject_explorer" -> user.subjectProgress.size >= achievement.requirement
                        else -> false
                    }
                    
                    if (isEarned) {
                        newAchievements.add(achievement)
                        
                        // Update user with new achievement
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val userRef = firestore.collection("users").document(currentUser.uid)
                            userRef.update(
                                "achievements", user.achievements + achievement.id,
                                "coins", user.coins + achievement.reward
                            ).await()
                        }
                    }
                }
            }
            
            Resource.Success(newAchievements)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to check achievements")
        }
    }
    
    // Helper functions
    private fun calculateLevel(totalScore: Int): Int {
        return when {
            totalScore < 100 -> 1
            totalScore < 300 -> 2
            totalScore < 600 -> 3
            totalScore < 1000 -> 4
            totalScore < 1500 -> 5
            totalScore < 2100 -> 6
            totalScore < 2800 -> 7
            totalScore < 3600 -> 8
            totalScore < 4500 -> 9
            else -> 10
        }
    }
    
    private fun calculateAverageScore(totalQuizzes: Int, currentAverage: Double, newScore: Int): Double {
        if (totalQuizzes == 0) return newScore.toDouble()
        return ((currentAverage * totalQuizzes) + newScore) / (totalQuizzes + 1)
    }
}
