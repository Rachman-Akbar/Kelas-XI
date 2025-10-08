package com.komputerkit.socialmedia.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.adapters.PreloadedImageAdapter
import com.komputerkit.socialmedia.databinding.ActivitySelectPreloadedImageBinding
import com.komputerkit.socialmedia.models.PreloadedImage
import com.komputerkit.socialmedia.viewmodels.PreloadedImageViewModel

class SelectPreloadedImageActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySelectPreloadedImageBinding
    private lateinit var imageAdapter: PreloadedImageAdapter
    private val viewModel: PreloadedImageViewModel by viewModels()
    
    private var imageType: String = "story" // "story" or "post"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivitySelectPreloadedImageBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            // Get image type from intent
            imageType = intent.getStringExtra("image_type") ?: "story"
            
            setupUI()
            setupRecyclerView()
            observeData()
            loadImages()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing image selection: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupUI() {
        binding.toolbarTitle.text = when (imageType) {
            "story" -> "Select Story Image"
            "post" -> "Select Post Image"
            else -> "Select Image"
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnCustomUpload.setOnClickListener {
            // Return to upload activity for custom image selection
            setResult(RESULT_CANCELED)
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        imageAdapter = PreloadedImageAdapter(imageType) { selectedImage ->
            onImageSelected(selectedImage)
        }
        
        binding.recyclerViewImages.apply {
            layoutManager = GridLayoutManager(this@SelectPreloadedImageActivity, 
                if (imageType == "story") 2 else 3)
            adapter = imageAdapter
        }
    }
    
    private fun observeData() {
        viewModel.preloadedImages.observe(this) { images ->
            binding.progressBar.visibility = android.view.View.GONE
            if (images.isEmpty()) {
                binding.textEmptyState.visibility = android.view.View.VISIBLE
                binding.textEmptyState.text = "No images available"
            } else {
                binding.textEmptyState.visibility = android.view.View.GONE
                imageAdapter.submitList(images)
            }
        }
        
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) 
                android.view.View.VISIBLE else android.view.View.GONE
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun loadImages() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        viewModel.loadPreloadedImages(imageType)
    }
    
    private fun onImageSelected(image: PreloadedImage) {
        val resultIntent = Intent().apply {
            putExtra("selected_image_url", image.url)
            putExtra("selected_image_id", image.id)
            putExtra("selected_image_category", image.category)
            putExtra("selected_image_title", image.title)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}