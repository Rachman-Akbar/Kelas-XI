package com.example.kishaapp.data.repository

import com.example.kishaapp.data.model.Category
import com.example.kishaapp.data.model.Product
import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MarketplaceRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore
) {

    fun observeProducts(): Flow<Result<List<Product>>> = callbackFlow {
        val listener = firestore.collection("products")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { it.toProduct() }.orEmpty()
                trySend(Result.success(items))
            }
        awaitClose { listener.remove() }
    }

    suspend fun getProductById(productId: String): Result<Product?> {
        return runCatching {
            val snap = firestore.collection("products").document(productId).get().await()
            snap.toProduct()
        }
    }

    fun observeCategories(): Flow<Result<List<Category>>> = callbackFlow {
        val listener = firestore.collection("categories")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val categories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                }.orEmpty()
                trySend(Result.success(categories))
            }
        awaitClose { listener.remove() }
    }

    suspend fun addProduct(product: Product): Result<Unit> {
        return runCatching {
            val docRef = firestore.collection("products").document()
            val payload = product.copy(
                id = docRef.id,
                createdAt = product.createdAt ?: Timestamp.now()
            )
            docRef.set(payload).await()
            Unit
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return runCatching {
            require(product.id.isNotBlank()) { "Product id wajib diisi" }
            firestore.collection("products").document(product.id).set(product).await()
            Unit
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return runCatching {
            firestore.collection("products").document(productId).delete().await()
            Unit
        }
    }

    private fun DocumentSnapshot.toProduct(): Product? {
        return toObject(Product::class.java)?.copy(id = id)
    }
}
