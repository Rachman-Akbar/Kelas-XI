package com.komputerkit.socialmedia.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

object ImageUtils {
    
    const val REQUEST_IMAGE_GALLERY = 1001
    const val REQUEST_IMAGE_CAMERA = 1002
    
    /**
     * Create intent to pick image from gallery
     */
    fun createGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }
    
    /**
     * Create intent to capture image from camera
     */
    fun createCameraIntent(activity: Activity): Intent? {
        return try {
            val photoFile = createImageFile(activity)
            val photoURI = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                photoFile
            )
            
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Create temporary image file
     */
    private fun createImageFile(activity: Activity): File {
        val timestamp = System.currentTimeMillis()
        val imageFileName = "IMG_$timestamp"
        val storageDir = activity.externalCacheDir
        
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }
    
    /**
     * Show image picker dialog options
     */
    fun showImagePickerOptions(
        activity: Activity,
        onGallerySelected: () -> Unit,
        onCameraSelected: () -> Unit
    ) {
        val options = arrayOf("Gallery", "Camera", "Cancel")
        
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> onGallerySelected()
                1 -> onCameraSelected()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }
    
    /**
     * Validate if URI points to a valid image
     */
    fun isValidImageUri(activity: Activity, uri: Uri?): Boolean {
        if (uri == null) return false
        
        return try {
            val inputStream = activity.contentResolver.openInputStream(uri)
            inputStream?.close()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get file size in MB
     */
    fun getFileSizeInMB(activity: Activity, uri: Uri): Double {
        return try {
            val inputStream = activity.contentResolver.openInputStream(uri)
            val bytes = inputStream?.available() ?: 0
            inputStream?.close()
            bytes / (1024.0 * 1024.0)
        } catch (e: Exception) {
            0.0
        }
    }
    
    /**
     * Check if image size is within limits (max 10MB)
     */
    fun isImageSizeValid(activity: Activity, uri: Uri, maxSizeMB: Double = 10.0): Boolean {
        return getFileSizeInMB(activity, uri) <= maxSizeMB
    }
    
    /**
     * Convert URI to base64 string with robust handling
     */
    fun convertUriToBase64(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // Use buffered approach to handle large images
                val buffer = ByteArray(8192)
                val output = java.io.ByteArrayOutputStream()
                var bytesRead: Int
                
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
                
                val bytes = output.toByteArray()
                output.close()
                
                "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Convert URI to base64 string without data prefix (clean base64) with compression
     */
    fun convertUriToCleanBase64(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // Decode bitmap with options for memory efficiency
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                
                // Get image dimensions first
                val tempStream = context.contentResolver.openInputStream(uri)
                android.graphics.BitmapFactory.decodeStream(tempStream, null, options)
                tempStream?.close()
                
                // Calculate sample size to reduce memory usage
                options.inSampleSize = calculateInSampleSize(options, 800, 800)
                options.inJustDecodeBounds = false
                
                // Decode the actual bitmap
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream, null, options)
                    ?: throw Exception("Failed to decode image")
                
                // Compress to JPEG with quality control
                val output = java.io.ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, output)
                val bytes = output.toByteArray()
                
                // Clean up
                bitmap.recycle()
                output.close()
                
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Calculate sample size for bitmap loading
     */
    private fun calculateInSampleSize(
        options: android.graphics.BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
}