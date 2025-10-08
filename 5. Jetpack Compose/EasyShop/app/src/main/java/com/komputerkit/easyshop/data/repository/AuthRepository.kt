package com.komputerkit.easyshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.model.User
import com.komputerkit.easyshop.utils.Resource
import com.komputerkit.easyshop.utils.FirebaseErrorHandler
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    suspend fun signUp(email: String, password: String, user: User): Resource<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID tidak ditemukan")
            
            // Simpan data user ke Firestore
            val userWithId = user.copy(id = userId)
            firestore.collection("users").document(userId).set(userWithId).await()
            
            Resource.Success("Registrasi berhasil!")
        } catch (e: Exception) {
            Resource.Error(getErrorMessage(e))
        }
    }
    
    suspend fun signIn(email: String, password: String): Resource<String> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success("Login berhasil!")
        } catch (e: Exception) {
            Resource.Error(getErrorMessage(e))
        }
    }
    
    suspend fun getCurrentUser(): Resource<User?> {
        return try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                val user = userDoc.toObject(User::class.java)
                Resource.Success(user)
            } else {
                Resource.Success(null)
            }
        } catch (e: Exception) {
            Resource.Error(getErrorMessage(e))
        }
    }
    
    fun signOut() {
        firebaseAuth.signOut()
    }
    
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    private fun getErrorMessage(exception: Exception): String {
        return FirebaseErrorHandler.getReadableErrorMessage(exception)
    }
}