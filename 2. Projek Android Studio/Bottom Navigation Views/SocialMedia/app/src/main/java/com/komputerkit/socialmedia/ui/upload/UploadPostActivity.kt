package com.komputerkit.socialmedia.ui.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.PostManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.manager.StoryManager
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.databinding.ActivityUploadPostBinding
import com.komputerkit.socialmedia.utils.ImageUtils
import kotlinx.coroutines.launch

class UploadPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadPostBinding
    private lateinit var authManager: AuthManager
    private lateinit var postManager: PostManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var storyManager: StoryManager
    
    private var selectedImageUri: Uri? = null
    private var isStoryUpload: Boolean = false

    // Activity result launchers
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImageSelected(uri)
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri?.let { uri ->
                handleImageSelected(uri)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            showPermissionDeniedDialog("Storage", "We need storage permission to access your photos.")
        }
    }

    // Multiple permissions launcher for Android 13+
    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val storageGranted = permissions[getStoragePermission()] ?: false
        
        when {
            cameraGranted && storageGranted -> {
                // Both permissions granted, show selection dialog
                showImageSourceDialog()
            }
            cameraGranted -> {
                openCamera()
            }
            storageGranted -> {
                openGallery()
            }
            else -> {
                showPermissionDeniedDialog("Camera and Storage", "We need both camera and storage permissions to upload images.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityUploadPostBinding.inflate(layoutInflater)
            setContentView(binding.root)

            initializeManagers()
            setupToolbar()
            setupClickListeners()
            updateUIForUploadType()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing upload: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeManagers() {
        authManager = AuthManager()
        postManager = PostManager(authManager)
        base64PostManager = Base64PostManager(authManager)
        storyManager = StoryManager(authManager)
        
        // Check if this is story upload
        isStoryUpload = intent.getStringExtra("upload_type") == "story"
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSelectImage.setOnClickListener {
                requestAllPermissions()
            }

            btnUpload.setOnClickListener {
                uploadPost()
            }
        }
    }

    private fun showImagePickerDialog() {
        ImageUtils.showImagePickerOptions(
            activity = this,
            onGallerySelected = {
                if (hasStoragePermission()) {
                    openGallery()
                } else {
                    requestStoragePermission()
                }
            },
            onCameraSelected = {
                if (hasCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
        )
    }

    private fun openGallery() {
        val intent = ImageUtils.createGalleryIntent()
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val intent = ImageUtils.createCameraIntent(this)
        if (intent != null) {
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleImageSelected(uri: Uri) {
        if (!ImageUtils.isValidImageUri(this, uri)) {
            Toast.makeText(this, "Invalid image selected", Toast.LENGTH_SHORT).show()
            return
        }

        if (!ImageUtils.isImageSizeValid(this, uri)) {
            Toast.makeText(this, "Image size must be less than 10MB", Toast.LENGTH_SHORT).show()
            return
        }

        selectedImageUri = uri
        
        // Load image into preview
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.ivPreview)

        // Hide select button and enable upload button
        binding.btnSelectImage.visibility = View.GONE
        binding.btnUpload.isEnabled = true
    }

    private fun uploadPost() {
        val caption = binding.etCaption.text.toString().trim()
        val imageUri = selectedImageUri
        
        if (imageUri == null) {
            Toast.makeText(this, "📷 Please select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        if (!authManager.isUserLoggedIn) {
            Toast.makeText(this, "🔐 Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (caption.isEmpty()) {
            Toast.makeText(this, "💬 Please add a caption", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (caption.isEmpty() && !isStoryUpload) {
            Toast.makeText(this, "Please add a caption", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                if (isStoryUpload) {
                    // Convert to base64 first
                    val base64Image = ImageUtils.convertUriToBase64(imageUri, this@UploadPostActivity)
                    if (base64Image.isNullOrEmpty()) {
                        showLoading(false)
                        showError("Failed to process image")
                        return@launch
                    }
                    
                    // Upload as story
                    val result = storyManager.uploadStoryWithBase64(
                        base64Image = base64Image,
                        caption = caption.ifEmpty { "" }
                    )
                    
                    showLoading(false)
                    
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(
                                this@UploadPostActivity,
                                "Story uploaded successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        is Result.Error -> {
                            showError("Story upload failed: ${result.exception.message}")
                        }
                        is Result.Loading -> {
                            showLoading(true)
                        }
                    }
                } else {
                    // Upload as post using Base64 (Firebase gratis)
                    showLoading(true)
                    val base64Image = ImageUtils.convertUriToCleanBase64(imageUri, this@UploadPostActivity)
                    
                    if (!base64Image.isNullOrEmpty()) {
                        base64PostManager.uploadPostWithBase64(base64Image, caption).collect { result ->
                            showLoading(false)
                            
                            when (result) {
                                is Result.Success -> {
                                    Toast.makeText(
                                        this@UploadPostActivity,
                                        "📸 Post uploaded successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                                is Result.Error -> {
                                    showError("❌ Upload failed: ${result.exception.message}")
                                }
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                            }
                        }
                    } else {
                        showLoading(false)
                        showError("❌ Failed to convert image to base64")
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                showError("Upload failed: ${e.message}")
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            btnUpload.isEnabled = !show && selectedImageUri != null
            btnSelectImage.isEnabled = !show
            etCaption.isEnabled = !show
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Permission checks
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun requestStoragePermission() {
        storagePermissionLauncher.launch(getStoragePermission())
    }
    
    private fun updateUIForUploadType() {
        if (isStoryUpload) {
            supportActionBar?.title = "Upload Story"
            binding.btnUpload.text = "Share Story"
            binding.etCaption.hint = "Add to your story..."
        } else {
            supportActionBar?.title = "Upload Post"
            binding.btnUpload.text = "Upload Post"
            binding.etCaption.hint = "Write a caption..."
        }
    }
    
    // Permission helper functions
    private fun getStoragePermission(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    
    private fun showPermissionDeniedDialog(permissionType: String, message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("$permissionType Permission Required")
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Permission denied. Cannot proceed with image selection.", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun openAppSettings() {
        try {
            val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = android.net.Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open settings", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()
        
        if (!hasCameraPermission()) {
            permissions.add(Manifest.permission.CAMERA)
        }
        
        if (!hasStoragePermission()) {
            permissions.add(getStoragePermission())
        }
        
        if (permissions.isNotEmpty()) {
            multiplePermissionsLauncher.launch(permissions.toTypedArray())
        } else {
            showImageSourceDialog()
        }
    }
    
    private fun showImageSourceDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("📷 Select Image Source")
            .setMessage("Choose where to get your image from:")
            .setPositiveButton("📱 Gallery") { _, _ ->
                if (hasStoragePermission()) {
                    openGallery()
                } else {
                    requestStoragePermission()
                }
            }
            .setNegativeButton("📸 Camera") { _, _ ->
                if (hasCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
            .setNeutralButton("Cancel", null)
            .create()
        
        dialog.show()
    }
}