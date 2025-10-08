package com.komputerkit.socialmedia.data.manager

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.User
import kotlinx.coroutines.tasks.await
class UserManager {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Search users by username
     */
    suspend fun searchUsers(query: String): Result<List<User>> {
        return try {
            // Simple search - in production, consider using full-text search
            val usersSnapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limit(20)
                .get()
                .await()
            
            val users = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            Result.Success(users)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User not found")
            
            Result.Success(user)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get user by username
     */
    suspend fun getUserByUsername(username: String): Result<User> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .await()
            
            if (querySnapshot.isEmpty) {
                throw Exception("User not found")
            }
            
            val user = querySnapshot.documents.first().toObject(User::class.java)
                ?: throw Exception("User data corrupted")
            
            Result.Success(user)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Follow/Unfollow user
     */
    suspend fun toggleFollowUser(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            if (currentUserId == targetUserId) {
                throw Exception("You cannot follow yourself")
            }
            
            val followRef = firestore.collection("follows").document("${currentUserId}_$targetUserId")
            val followDoc = followRef.get().await()
            
            val isFollowing = followDoc.exists()
            
            if (isFollowing) {
                // Unfollow
                firestore.runBatch { batch ->
                    batch.delete(followRef)
                    
                    // Update follower count
                    val currentUserRef = firestore.collection("users").document(currentUserId)
                    val targetUserRef = firestore.collection("users").document(targetUserId)
                    
                    batch.update(currentUserRef, "followingCount", com.google.firebase.firestore.FieldValue.increment(-1))
                    batch.update(targetUserRef, "followersCount", com.google.firebase.firestore.FieldValue.increment(-1))
                }.await()
                
                Result.Success(false) // Not following anymore
                
            } else {
                // Follow
                val followData = mapOf(
                    "followerId" to currentUserId,
                    "followingId" to targetUserId,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                
                firestore.runBatch { batch ->
                    batch.set(followRef, followData)
                    
                    // Update follower count
                    val currentUserRef = firestore.collection("users").document(currentUserId)
                    val targetUserRef = firestore.collection("users").document(targetUserId)
                    
                    batch.update(currentUserRef, "followingCount", com.google.firebase.firestore.FieldValue.increment(1))
                    batch.update(targetUserRef, "followersCount", com.google.firebase.firestore.FieldValue.increment(1))
                }.await()
                
                Result.Success(true) // Now following
            }
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Check if user is following another user
     */
    suspend fun isFollowing(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            val followDoc = firestore.collection("follows")
                .document("${currentUserId}_$targetUserId")
                .get()
                .await()
            
            Result.Success(followDoc.exists())
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get user's followers
     */
    suspend fun getUserFollowers(userId: String): Result<List<User>> {
        return try {
            val followersSnapshot = firestore.collection("follows")
                .whereEqualTo("followingId", userId)
                .get()
                .await()
            
            val followerIds = followersSnapshot.documents.map { doc ->
                doc.getString("followerId") ?: ""
            }.filter { it.isNotEmpty() }
            
            if (followerIds.isEmpty()) {
                return Result.Success(emptyList())
            }
            
            val usersSnapshot = firestore.collection("users")
                .whereIn("userId", followerIds)
                .get()
                .await()
            
            val followers = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            Result.Success(followers)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get users that the user is following
     */
    suspend fun getUserFollowing(userId: String): Result<List<User>> {
        return try {
            val followingSnapshot = firestore.collection("follows")
                .whereEqualTo("followerId", userId)
                .get()
                .await()
            
            val followingIds = followingSnapshot.documents.map { doc ->
                doc.getString("followingId") ?: ""
            }.filter { it.isNotEmpty() }
            
            if (followingIds.isEmpty()) {
                return Result.Success(emptyList())
            }
            
            val usersSnapshot = firestore.collection("users")
                .whereIn("userId", followingIds)
                .get()
                .await()
            
            val following = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            Result.Success(following)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get suggested users (users with most followers that current user is not following)
     */
    suspend fun getSuggestedUsers(currentUserId: String, limit: Long = 10): Result<List<User>> {
        return try {
            // Get users that current user is following
            val followingResult = getUserFollowing(currentUserId)
            val followingIds = if (followingResult is Result.Success) {
                followingResult.data.map { it.userId }.toSet()
            } else {
                emptySet()
            }
            
            // Get users ordered by followers count
            val usersSnapshot = firestore.collection("users")
                .orderBy("followersCount", Query.Direction.DESCENDING)
                .limit(limit * 2) // Get more to filter out following users
                .get()
                .await()
            
            val suggestedUsers = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }.filter { user ->
                user.userId != currentUserId && !followingIds.contains(user.userId)
            }.take(limit.toInt())
            
            Result.Success(suggestedUsers)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Block user
     */
    suspend fun blockUser(currentUserId: String, targetUserId: String): Result<Unit> {
        return try {
            if (currentUserId == targetUserId) {
                throw Exception("You cannot block yourself")
            }
            
            val blockData = mapOf(
                "blockerId" to currentUserId,
                "blockedId" to targetUserId,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection("blocks")
                .document("${currentUserId}_$targetUserId")
                .set(blockData)
                .await()
            
            // Also unfollow if following
            val followRef = firestore.collection("follows").document("${currentUserId}_$targetUserId")
            followRef.delete().await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Unblock user
     */
    suspend fun unblockUser(currentUserId: String, targetUserId: String): Result<Unit> {
        return try {
            firestore.collection("blocks")
                .document("${currentUserId}_$targetUserId")
                .delete()
                .await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Check if user is blocked
     */
    suspend fun isUserBlocked(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            val blockDoc = firestore.collection("blocks")
                .document("${currentUserId}_$targetUserId")
                .get()
                .await()
            
            Result.Success(blockDoc.exists())
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get blocked users
     */
    suspend fun getBlockedUsers(currentUserId: String): Result<List<User>> {
        return try {
            val blocksSnapshot = firestore.collection("blocks")
                .whereEqualTo("blockerId", currentUserId)
                .get()
                .await()
            
            val blockedIds = blocksSnapshot.documents.map { doc ->
                doc.getString("blockedId") ?: ""
            }.filter { it.isNotEmpty() }
            
            if (blockedIds.isEmpty()) {
                return Result.Success(emptyList())
            }
            
            val usersSnapshot = firestore.collection("users")
                .whereIn("userId", blockedIds)
                .get()
                .await()
            
            val blockedUsers = usersSnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            
            Result.Success(blockedUsers)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}