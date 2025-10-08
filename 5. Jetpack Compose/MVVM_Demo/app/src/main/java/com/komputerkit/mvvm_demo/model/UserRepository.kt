package com.komputerkit.mvvm_demo.model

import kotlinx.coroutines.delay

/**
 * Repository class to handle user data operations
 */
class UserRepository {
    
    /**
     * Fetches user data with a mock delay of 2000ms
     * @return UserData instance with hardcoded values
     */
    suspend fun fetchUserData(): UserData {
        // Simulate network delay
        delay(2000)
        
        // Return hardcoded user data
        return UserData(
            name = "John",
            age = 25
        )
    }
}