package com.komputerkit.adminwof.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.adminwof.data.model.OrderModel
import com.komputerkit.adminwof.data.model.UserModel
import com.komputerkit.adminwof.utils.Constants
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk Order Management operations
 */
class OrderRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection(Constants.COLLECTION_ORDERS)
    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)
    
    /**
     * Fetch all orders (sorted by date descending)
     */
    suspend fun fetchAllOrders(): Result<List<OrderModel>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch order by ID
     */
    suspend fun fetchOrderById(orderId: String): Result<OrderModel> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            val order = doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
            
            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch user by ID
     */
    suspend fun fetchUserById(userId: String): Result<UserModel> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(UserModel::class.java)?.copy(id = doc.id)
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", newStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
