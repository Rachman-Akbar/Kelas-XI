package com.komputerkit.earningapp.data.model

/**
 * User profile data model for Firestore
 */
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val level: Int = 1,
    val experience: Int = 0,
    val coins: Int = 0,
    val totalEarnings: Double = 0.0,
    val profileImageUrl: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    
    // App-specific fields
    val completedQuizzes: Int = 0,
    val completedTasks: Int = 0,
    val referralCode: String = "",
    val referredBy: String? = null,
    val achievements: List<String> = emptyList(),
    val preferences: Map<String, Any> = emptyMap()
) {
    /**
     * Convert to HashMap for Firestore
     */
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "uid" to uid,
            "email" to email,
            "displayName" to displayName,
            "createdAt" to createdAt,
            "lastLoginAt" to lastLoginAt,
            "isActive" to isActive,
            "level" to level,
            "experience" to experience,
            "coins" to coins,
            "totalEarnings" to totalEarnings,
            "profileImageUrl" to (profileImageUrl ?: ""),
            "phoneNumber" to (phoneNumber ?: ""),
            "dateOfBirth" to (dateOfBirth ?: ""),
            "gender" to (gender ?: ""),
            "completedQuizzes" to completedQuizzes,
            "completedTasks" to completedTasks,
            "referralCode" to referralCode,
            "referredBy" to (referredBy ?: ""),
            "achievements" to achievements,
            "preferences" to preferences
        )
    }
    
    companion object {
        /**
         * Create UserProfile from Firestore document
         */
        @Suppress("UNCHECKED_CAST")
        fun fromMap(data: Map<String, Any>): UserProfile {
            return UserProfile(
                uid = data["uid"] as? String ?: "",
                email = data["email"] as? String ?: "",
                displayName = data["displayName"] as? String ?: "",
                createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis(),
                lastLoginAt = data["lastLoginAt"] as? Long ?: System.currentTimeMillis(),
                isActive = data["isActive"] as? Boolean ?: true,
                level = (data["level"] as? Number)?.toInt() ?: 1,
                experience = (data["experience"] as? Number)?.toInt() ?: 0,
                coins = (data["coins"] as? Number)?.toInt() ?: 0,
                totalEarnings = (data["totalEarnings"] as? Number)?.toDouble() ?: 0.0,
                profileImageUrl = data["profileImageUrl"] as? String,
                phoneNumber = data["phoneNumber"] as? String,
                dateOfBirth = data["dateOfBirth"] as? String,
                gender = data["gender"] as? String,
                completedQuizzes = (data["completedQuizzes"] as? Number)?.toInt() ?: 0,
                completedTasks = (data["completedTasks"] as? Number)?.toInt() ?: 0,
                referralCode = data["referralCode"] as? String ?: "",
                referredBy = data["referredBy"] as? String,
                achievements = data["achievements"] as? List<String> ?: emptyList(),
                preferences = data["preferences"] as? Map<String, Any> ?: emptyMap()
            )
        }
        
        /**
         * Create UserProfile from AuthUser
         */
        fun fromAuthUser(authUser: AuthUser): UserProfile {
            val referralCode = generateReferralCode(authUser.uid)
            return UserProfile(
                uid = authUser.uid,
                email = authUser.email ?: "",
                displayName = authUser.displayName ?: "",
                referralCode = referralCode
            )
        }
        
        /**
         * Generate unique referral code from user ID
         */
        private fun generateReferralCode(uid: String): String {
            return "REF" + uid.takeLast(6).uppercase()
        }
    }
}
