package com.komputerkit.socialmedia.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.databinding.ActivityPostDetailBinding
import com.komputerkit.socialmedia.ui.adapter.PostAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PostDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPostDetailBinding
    private lateinit var authManager: AuthManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var postAdapter: PostAdapter
    
    private var currentPost: Post? = null
    
    companion object {
        private const val EXTRA_POST_ID = "extra_post_id"
        
        fun newIntent(context: Context, postId: String): Intent {
            return Intent(context, PostDetailActivity::class.java).apply {
                putExtra(EXTRA_POST_ID, postId)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupManagers()
        setupViews()
        loadPostDetail()
    }
    
    private fun setupManagers() {
        authManager = AuthManager()
        base64PostManager = Base64PostManager(authManager)
        
        val currentUserId = authManager.currentUser?.uid ?: ""
        postAdapter = PostAdapter(
            onLikeClick = ::handleLikeClick,
            onCommentClick = ::handleCommentClick,
            onShareClick = ::handleShareClick,
            onBookmarkClick = ::handleBookmarkClick,
            onProfileClick = ::handleProfileClick,
            onMenuClick = ::handleMenuClick,
            currentUserId = currentUserId
        )
    }
    
    private fun setupViews() {
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // Setup RecyclerView for single post
        binding.rvPostDetail.apply {
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = postAdapter
        }
    }
    
    private fun loadPostDetail() {
        val postId = intent.getStringExtra(EXTRA_POST_ID)
        if (postId.isNullOrEmpty()) {
            Log.e("PostDetailActivity", "No post ID provided")
            finish()
            return
        }
        
        Log.d("PostDetailActivity", "Loading post detail for ID: $postId")
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            base64PostManager.getPostById(postId) { result ->
                when (result) {
                    is Result.Success -> {
                        val post = result.data
                        if (post != null) {
                            currentPost = post
                            Log.d("PostDetailActivity", "Post loaded successfully: ${post.postId}")
                            
                            // Show single post in RecyclerView
                            postAdapter.submitList(listOf(post))
                            binding.progressBar.visibility = View.GONE
                            binding.rvPostDetail.visibility = View.VISIBLE
                        } else {
                            showError("Post not found")
                            finish()
                        }
                    }
                    is Result.Error -> {
                        Log.e("PostDetailActivity", "Error loading post: ${result.exception.message}")
                        showError("Failed to load post: ${result.exception.message}")
                        finish()
                    }
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    
    // Post interaction handlers
    private fun handleLikeClick(post: Post) {
        if (!authManager.isUserLoggedIn) {
            Log.d("PostDetailActivity", "User not logged in for like action")
            return
        }

        lifecycleScope.launch {
            try {
                base64PostManager.likePost(post.postId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            Log.d("PostDetailActivity", "Post liked successfully")
                            // Reload post to get updated like status
                            loadPostDetail()
                        }
                        is Result.Error -> {
                            showError("Failed to like post: ${result.exception.message}")
                            Log.e("PostDetailActivity", "Error liking post", result.exception)
                        }
                        is Result.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Error liking post: ${e.message}")
                Log.e("PostDetailActivity", "Unexpected error liking post", e)
            }
        }
    }
    
    private fun handleCommentClick(post: Post) {
        Toast.makeText(this, "Comments feature coming soon", Toast.LENGTH_SHORT).show()
        // TODO: Navigate to comments screen or show comments dialog
    }
    
    private fun handleShareClick(post: Post) {
        Toast.makeText(this, "Share feature coming soon", Toast.LENGTH_SHORT).show()
        // TODO: Implement share functionality
    }
    
    private fun handleBookmarkClick(post: Post) {
        Toast.makeText(this, "Bookmark feature coming soon", Toast.LENGTH_SHORT).show()
        // TODO: Implement bookmark functionality
    }
    
    private fun handleProfileClick(post: Post) {
        Toast.makeText(this, "View profile @${post.username}", Toast.LENGTH_SHORT).show()
        // TODO: Navigate to user profile
    }
    
    private fun handleMenuClick(post: Post) {
        Toast.makeText(this, "Post options for @${post.username}", Toast.LENGTH_SHORT).show()
        // TODO: Show post options menu
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}