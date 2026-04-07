package com.example.kishaapp.data.repository

import android.net.Uri
import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository(
    private val storage: FirebaseStorage = FirebaseProvider.storage
) {

    suspend fun uploadProductImage(imageUri: Uri): Result<String> {
        return runCatching {
            val imageRef = storage.reference
                .child("product_images")
                .child("${UUID.randomUUID()}.jpg")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        }
    }
}
