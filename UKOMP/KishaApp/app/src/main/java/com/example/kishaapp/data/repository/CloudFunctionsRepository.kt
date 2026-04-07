package com.example.kishaapp.data.repository

import android.util.Log
import com.example.kishaapp.data.model.UserProfile
import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

class CloudFunctionsRepository(
    private val functions: FirebaseFunctions = FirebaseProvider.functions
) {

    /**
     * Validate user profile on server
     */
    suspend fun validateUserProfile(
        name: String,
        email: String,
        role: String = "customer"
    ): Result<Map<String, Any>> {
        return runCatching {
            val data = hashMapOf(
                "name" to name,
                "email" to email,
                "role" to role
            )
            val result = functions
                .getHttpsCallable("validateUserProfile")
                .call(data)
                .await()
            result.data as Map<String, Any>
        }
    }

    /**
     * Validate product on server before creating
     */
    suspend fun validateProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        stock: Int
    ): Result<Map<String, Any>> {
        return runCatching {
            val data = hashMapOf(
                "name" to name,
                "description" to description,
                "price" to price,
                "category" to category,
                "stock" to stock
            )
            val result = functions
                .getHttpsCallable("validateProduct")
                .call(data)
                .await()
            result.data as Map<String, Any>
        }
    }

    /**
     * Create product via cloud function (server creates with seller ID)
     */
    suspend fun createProduct(
        name: String,
        description: String,
        price: Double,
        category: String,
        stock: Int,
        imageUrl: String? = null
    ): Result<Map<String, Any>> {
        return runCatching {
            val data = hashMapOf(
                "name" to name,
                "description" to description,
                "price" to price,
                "category" to category,
                "stock" to stock,
                "imageUrl" to (imageUrl ?: "")
            )
            val result = functions
                .getHttpsCallable("createProduct")
                .call(data)
                .await()
            result.data as Map<String, Any>
        }
    }

    /**
     * Create order via cloud function
     */
    suspend fun createOrder(
        productId: String,
        quantity: Int,
        totalPrice: Double
    ): Result<Map<String, Any>> {
        return runCatching {
            val data = hashMapOf(
                "productId" to productId,
                "quantity" to quantity,
                "totalPrice" to totalPrice
            )
            val result = functions
                .getHttpsCallable("createOrder")
                .call(data)
                .await()
            result.data as Map<String, Any>
        }
    }

    /**
     * Get marketplace statistics (admin only)
     */
    suspend fun getStatistics(): Result<Map<String, Any>> {
        return runCatching {
            val result = functions
                .getHttpsCallable("getStatistics")
                .call(Unit)
                .await()
            result.data as Map<String, Any>
        }
    }
}
