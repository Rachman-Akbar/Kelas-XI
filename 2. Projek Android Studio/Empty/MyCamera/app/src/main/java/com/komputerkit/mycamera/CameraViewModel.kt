package com.komputerkit.mycamera

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CameraViewModel : ViewModel() {
    private val _lastCapturedUri = MutableStateFlow<Uri?>(null)
    val lastCapturedUri: StateFlow<Uri?> = _lastCapturedUri

    private val _isCapturing = MutableStateFlow(false)
    val isCapturing: StateFlow<Boolean> = _isCapturing

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera

    private val _showSuccessMessage = MutableStateFlow<String?>(null)
    val showSuccessMessage: StateFlow<String?> = _showSuccessMessage

    private val _flashMode = MutableStateFlow(FlashMode.OFF)
    val flashMode: StateFlow<FlashMode> = _flashMode

    val imageCapture = ImageCapture.Builder().build()

    enum class FlashMode {
        OFF, ON, AUTO
    }

    fun bindCamera(context: Context, bind: (ProcessCameraProvider, Preview) -> Unit) {
        viewModelScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build()
            bind(cameraProvider, preview)
        }
    }

    fun toggleFlash() {
        _flashMode.value = when (_flashMode.value) {
            FlashMode.OFF -> FlashMode.ON
            FlashMode.ON -> FlashMode.AUTO
            FlashMode.AUTO -> FlashMode.OFF
        }
        
        updateFlashMode()
    }

    private fun updateFlashMode() {
        imageCapture.flashMode = when (_flashMode.value) {
            FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
            FlashMode.ON -> ImageCapture.FLASH_MODE_ON
            FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
        }
    }

    fun toggleCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun shareImage(context: Context, uri: Uri?) {
        uri?.let {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/jpeg"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Foto"))
        }
    }

    fun deleteImage(context: Context, uri: Uri?) {
        uri?.let {
            viewModelScope.launch {
                try {
                    val deleted = context.contentResolver.delete(it, null, null)
                    if (deleted > 0) {
                        _lastCapturedUri.value = null
                        _showSuccessMessage.value = "Foto berhasil dihapus"
                        Toast.makeText(context, "Foto berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal menghapus foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun editImage(context: Context, uri: Uri?) {
        uri?.let {
            try {
                val editIntent = Intent(Intent.ACTION_EDIT).apply {
                    setDataAndType(it, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                
                // Coba buka dengan aplikasi edit foto yang tersedia
                val chooser = Intent.createChooser(editIntent, "Edit Foto dengan")
                context.startActivity(chooser)
            } catch (e: Exception) {
                // Jika tidak ada aplikasi edit, buka gallery
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(it, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                try {
                    context.startActivity(viewIntent)
                    Toast.makeText(context, "Buka dengan aplikasi edit foto", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Toast.makeText(context, "Tidak dapat membuka editor foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openGallery(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
        }
        context.startActivity(intent)
    }

    fun clearSuccessMessage() {
        _showSuccessMessage.value = null
    }



    fun takePhoto(context: Context, onPhotoTaken: () -> Unit) {
        _isCapturing.value = true
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    _lastCapturedUri.value = output.savedUri
                    _isCapturing.value = false
                    _showSuccessMessage.value = "Foto berhasil disimpan"
                    Toast.makeText(context, "Foto berhasil disimpan", Toast.LENGTH_SHORT).show()
                    onPhotoTaken()
                }

                override fun onError(exception: ImageCaptureException) {
                    _isCapturing.value = false
                    Toast.makeText(context, "Gagal mengambil foto: ${exception.message}", Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
            }
        )
    }
}