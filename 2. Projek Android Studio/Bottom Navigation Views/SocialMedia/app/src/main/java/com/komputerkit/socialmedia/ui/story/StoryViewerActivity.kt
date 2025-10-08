package com.komputerkit.socialmedia.ui.story

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.StoryManager
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.Story
import com.komputerkit.socialmedia.databinding.ActivityStoryViewerBinding
import kotlinx.coroutines.launch

class StoryViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryViewerBinding
    private lateinit var authManager: AuthManager
    private lateinit var storyManager: StoryManager
    
    private var stories: List<Story> = emptyList()
    private var currentStoryIndex = 0
    private var currentStory: Story? = null
    
    // Story progress timer
    private var storyTimer: android.os.CountDownTimer? = null
    private val STORY_DURATION = 5000L // 5 seconds per story
    private var progressHandler: android.os.Handler? = null
    private var progressRunnable: Runnable? = null

    companion object {
        const val EXTRA_STORY_USER_ID = "story_user_id"
        const val EXTRA_STORY_INDEX = "story_index"
        const val EXTRA_STORY_ID = "story_id"
        const val EXTRA_USERNAME = "username"
        private const val PROGRESS_UPDATE_INTERVAL = 50L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityStoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide system UI for immersive experience
        hideSystemUI()
        
        initializeManagers()
        setupClickListeners()
        loadStories()
    }
    
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }
    
    private fun initializeManagers() {
        authManager = AuthManager()
        storyManager = StoryManager(authManager)
    }
    
    private fun setupClickListeners() {
        // Close button
        binding.ivClose.setOnClickListener {
            finish()
        }
        
        // Enhanced touch handling for navigation with gesture detection
        var gestureDetector = android.view.GestureDetector(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val screenWidth = binding.storyContainer.width
                val tapX = e.x
                
                when {
                    tapX < screenWidth / 2 -> {
                        // Left half - previous story
                        showPreviousStory()
                    }
                    else -> {
                        // Right half - next story
                        showNextStory()
                    }
                }
                return true
            }
            
            override fun onLongPress(e: MotionEvent) {
                // Long press to pause story
                pauseStoryTimer()
            }
        })
        
        binding.storyContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            
            // Resume timer when touch is released
            if (event.action == MotionEvent.ACTION_UP) {
                resumeStoryTimer()
            }
            true
        }
    }
    
    private fun loadStories() {
        val userId = intent.getStringExtra(EXTRA_STORY_USER_ID)
        currentStoryIndex = intent.getIntExtra(EXTRA_STORY_INDEX, 0)
        
        if (userId == null) {
            Toast.makeText(this, "Story not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        lifecycleScope.launch {
            storyManager.getActiveStories().collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Find stories for the specific user
                        stories = result.data.filter { it.userId == userId }
                        if (stories.isNotEmpty()) {
                            showStory(currentStoryIndex)
                        } else {
                            Toast.makeText(this@StoryViewerActivity, "No stories found", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(this@StoryViewerActivity, "Failed to load stories", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is Result.Loading -> {
                        // Show loading
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    
    private fun showStory(index: Int) {
        if (index < 0 || index >= stories.size) {
            finish()
            return
        }
        
        currentStoryIndex = index
        currentStory = stories[index]
        
        binding.progressBar.visibility = View.GONE
        
        // Update UI with enhanced features
        currentStory?.let { story ->
            // Load story image with Base64 support
            try {
                if (story.imageBase64.isNotEmpty()) {
                    val imageBytes = android.util.Base64.decode(story.imageBase64, android.util.Base64.DEFAULT)
                    Glide.with(this)
                        .load(imageBytes)
                        .centerCrop()
                        .into(binding.ivStoryImage)
                } else if (story.imageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(story.imageUrl)
                        .centerCrop()
                        .into(binding.ivStoryImage)
                } else {
                    binding.ivStoryImage.setImageResource(com.komputerkit.socialmedia.R.drawable.ic_image_placeholder)
                }
            } catch (e: Exception) {
                binding.ivStoryImage.setImageResource(com.komputerkit.socialmedia.R.drawable.ic_image_placeholder)
            }
            
            // Set user info with timestamp
            binding.tvUsername.text = story.username
            binding.tvTimestamp.text = formatTimestamp(story.timestamp)
            
            // Load profile image with Base64 support
            try {
                if (story.userProfileImageBase64.isNotEmpty()) {
                    val profileImageBytes = android.util.Base64.decode(story.userProfileImageBase64, android.util.Base64.DEFAULT)
                    Glide.with(this)
                        .load(profileImageBytes)
                        .circleCrop()
                        .into(binding.ivProfileImage)
                } else if (story.userProfileImageUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(story.userProfileImageUrl)
                        .circleCrop()
                        .into(binding.ivProfileImage)
                } else {
                    binding.ivProfileImage.setImageResource(com.komputerkit.socialmedia.R.drawable.ic_profile_placeholder)
                }
            } catch (e: Exception) {
                binding.ivProfileImage.setImageResource(com.komputerkit.socialmedia.R.drawable.ic_profile_placeholder)
            }
            
            // Set story text if available
            if (story.text.isNotEmpty()) {
                binding.tvStoryText.text = story.text
                binding.tvStoryText.visibility = View.VISIBLE
            } else {
                binding.tvStoryText.visibility = View.GONE
            }
            
            // Update progress indicators
            updateProgressIndicators()
            
            // Start enhanced timer for auto advance
            startEnhancedStoryTimer()
            
            // Mark story as viewed
            val currentUserId = authManager.currentUser?.uid
            if (currentUserId != null) {
                lifecycleScope.launch {
                    try {
                        storyManager.markStoryAsViewed(story.storyId, currentUserId)
                    } catch (e: Exception) {
                        // Silent fail for marking viewed
                    }
                }
            }
        }
    }
    
    private fun updateProgressIndicators() {
        binding.progressContainer.removeAllViews()
        
        for (i in stories.indices) {
            val progressBar = View(this).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    0,
                    8,
                    1f
                ).apply {
                    marginEnd = if (i < stories.size - 1) 8 else 0
                }
                setBackgroundColor(
                    if (i < currentStoryIndex) 0xFFFFFFFF.toInt()
                    else if (i == currentStoryIndex) 0x80FFFFFF.toInt()
                    else 0x40FFFFFF.toInt()
                )
            }
            binding.progressContainer.addView(progressBar)
        }
    }
    
    private fun startEnhancedStoryTimer() {
        storyTimer?.cancel()
        storyTimer = object : android.os.CountDownTimer(STORY_DURATION, 100) {
            override fun onTick(millisUntilFinished: Long) {
                val progress = (STORY_DURATION - millisUntilFinished).toFloat() / STORY_DURATION
                updateCurrentProgressBar(progress)
            }
            
            override fun onFinish() {
                showNextStory()
            }
        }.start()
    }
    
    private fun pauseStoryTimer() {
        storyTimer?.cancel()
    }
    
    private fun resumeStoryTimer() {
        // Calculate remaining time and resume
        val currentProgressBar = binding.progressContainer.getChildAt(currentStoryIndex)
        val currentProgress = currentProgressBar.scaleX
        val remainingTime = ((1f - currentProgress) * STORY_DURATION).toLong()
        
        if (remainingTime > 0) {
            storyTimer?.cancel()
            storyTimer = object : android.os.CountDownTimer(remainingTime, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = currentProgress + ((remainingTime - millisUntilFinished).toFloat() / remainingTime) * (1f - currentProgress)
                    updateCurrentProgressBar(progress)
                }
                
                override fun onFinish() {
                    showNextStory()
                }
            }.start()
        }
    }
    
    private fun updateCurrentProgressBar(progress: Float) {
        if (currentStoryIndex < binding.progressContainer.childCount) {
            val progressBar = binding.progressContainer.getChildAt(currentStoryIndex)
            progressBar.scaleX = progress
        }
    }
    
    private fun showNextStory() {
        if (currentStoryIndex < stories.size - 1) {
            showStory(currentStoryIndex + 1)
        } else {
            finish()
        }
    }
    
    private fun showPreviousStory() {
        if (currentStoryIndex > 0) {
            showStory(currentStoryIndex - 1)
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            else -> "${diff / (24 * 60 * 60 * 1000)}d ago"
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        storyTimer?.cancel()
        progressHandler?.removeCallbacks(progressRunnable ?: return)
    }
    
    override fun onPause() {
        super.onPause()
        pauseStoryTimer()
    }
    
    override fun onResume() {
        super.onResume()
        if (currentStory != null && stories.isNotEmpty()) {
            resumeStoryTimer()
        }
    }
}