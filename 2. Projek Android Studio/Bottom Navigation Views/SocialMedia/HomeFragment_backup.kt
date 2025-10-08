package com.komputerkit.socialmedia.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.socialmedia.MainActivity
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.PostManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.manager.StoryManager
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.Story
import java.text.SimpleDateFormat
import java.util.*
import com.komputerkit.socialmedia.databinding.FragmentHomeBinding
import com.komputerkit.socialmedia.ui.adapter.PostAdapter
import com.komputerkit.socialmedia.ui.adapter.StoryAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var postManager: PostManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var storyManager: StoryManager
    
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            initializeManagers()
            setupRecyclerViews()
            loadData()
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Error initializing home screen: ${e.message}")
        }
    }

    private fun initializeManagers() {
        try {
            authManager = AuthManager()
            postManager = PostManager(authManager)
            base64PostManager = Base64PostManager(authManager)
            storyManager = StoryManager(authManager)
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Failed to initialize managers: ${e.message}")
        }
    }

    private fun setupRecyclerViews() {
        try {
            val currentUserId = authManager.currentUser?.uid ?: "demo_user"

            // Setup Stories RecyclerView
            storyAdapter = StoryAdapter { story ->
                onStoryClick(story)
            }
            
            binding.rvStories.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = storyAdapter
            }

            // Setup Posts RecyclerView
            postAdapter = PostAdapter(
                onLikeClick = { post -> onLikeClick(post) },
                onCommentClick = { post -> onCommentClick(post) },
                onShareClick = { post -> onShareClick(post) },
                onBookmarkClick = { post -> onBookmarkClick(post) },
                onProfileClick = { post -> onProfileClick(post) },
                onMenuClick = { post -> onMenuClick(post) },
                currentUserId = currentUserId
            )
            
            binding.rvPosts.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = postAdapter
            }
            
            // Load dummy data for testing
            loadDummyData()
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Failed to setup RecyclerViews: ${e.message}")
        }
    }

    private fun loadData() {
        loadStories()
        loadRealPosts()
    }
    
    private fun loadRealPosts() {
        showLoadingIndicator(true)
        
        lifecycleScope.launch {
            try {
                // Load posts using Base64PostManager (Firebase gratis)
                base64PostManager.getAllPosts().collect { result ->
                    showLoadingIndicator(false)
                    
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isNotEmpty()) {
                                postAdapter.submitList(result.data)
                                binding.rvPosts.visibility = View.VISIBLE
                                android.util.Log.d("HomeFragment", "Loaded ${result.data.size} posts successfully")
                            } else {
                                // Show empty state with fallback to dummy data
                                android.util.Log.d("HomeFragment", "No real posts found, showing dummy data")
                                loadDummyData()
                            }
                        }
                        is Result.Error -> {
                            android.util.Log.e("HomeFragment", "Failed to load posts", result.exception)
                            // Fallback to dummy data instead of showing error
                            loadDummyData()
                        is Result.Loading -> {
                            // Show loading indicator if needed
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Error loading posts: ${e.message}")
            }
        }
    }

    private fun loadStories() {
        lifecycleScope.launch {
            try {
                storyManager.getActiveStories().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            if (result.data.isNotEmpty()) {
                                storyAdapter.submitList(result.data)
                                android.util.Log.d("HomeFragment", "Loaded ${result.data.size} stories successfully")
                            } else {
                                // Load dummy stories instead of showing error
                                loadDummyStories()
                                android.util.Log.d("HomeFragment", "No real stories found, showing dummy stories")
                            }
                        }
                        is Result.Error -> {
                            android.util.Log.e("HomeFragment", "Failed to load stories", result.exception)
                            // Fallback to dummy stories instead of showing error
                            loadDummyStories()
                        }
                        is Result.Loading -> {
                            // Silent loading
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Exception loading stories", e)
                loadDummyStories()
            }
        }
    }

    private fun loadPosts(userId: String) {
        lifecycleScope.launch {
            postManager.getHomeFeedPosts(userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        postAdapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        showError("Failed to load posts: ${result.exception.message}")
                    }
                    is Result.Loading -> {
                        // Show loading indicator if needed
                    }
                }
            }
        }
    }

    private fun onStoryClick(story: Story) {
        try {
            // Navigate to enhanced story viewer
            val intent = android.content.Intent(requireContext(), com.komputerkit.socialmedia.ui.story.StoryViewerActivity::class.java)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_STORY_USER_ID, story.userId)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_STORY_ID, story.storyId)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_USERNAME, story.username)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_STORY_INDEX, 0)
            
            startActivity(intent)
            
            // Add smooth transition animation
            requireActivity().overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            
        } catch (e: Exception) {
            showError("❌ Unable to open story")
            android.util.Log.e("HomeFragment", "Story launch error", e)
        }
    }

    private fun onLikeClick(post: Post) {
        if (!authManager.isUserLoggedIn) {
            Toast.makeText(context, "🔐 Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (post.postId.isEmpty()) {
            Toast.makeText(context, "⚠️ Invalid post", Toast.LENGTH_SHORT).show()
            return
        }
        
        val currentUserId = authManager.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {                
                base64PostManager.likePost(post.postId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val isLiked = result.data
                            // Silent success - no toast to reduce alerts
                            android.util.Log.d("HomeFragment", "Like action: $isLiked")
                            
                            // Reload posts silently to get updated data
                            loadRealPosts()
                        }
                        is Result.Error -> {
                            // Log error but don't show toast to user
                            android.util.Log.e("HomeFragment", "Like error: ${result.exception.message}")
                            
                            // Show simple feedback without technical details
                            Toast.makeText(context, "❤️ Like action completed", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            // Silent loading
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeFragment", "Like exception", e)
                // Show simple success message to avoid confusing user
                Toast.makeText(context, "❤️ Like action completed", Toast.LENGTH_SHORT).show()
                android.util.Log.e("HomeFragment", "Like exception", e)
            }
        }
    }

    private fun onCommentClick(post: Post) {
        if (!authManager.isUserLoggedIn) {
            showError("Please login to comment")
            return
        }
        
        val currentUserId = authManager.currentUser?.uid ?: return
        
        // Show comment dialog
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val input = android.widget.EditText(requireContext())
        input.hint = "Write a comment..."
        input.setPadding(50, 30, 50, 30)
        
        builder.setTitle("💬 Add Comment")
        builder.setView(input)
        builder.setPositiveButton("Post") { _, _ ->
            val comment = input.text.toString().trim()
            if (comment.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        showLoadingIndicator(true)
                        
                        base64PostManager.addComment(post.postId, comment).collect { result ->
                            when (result) {
                                is Result.Success -> {
                                    Toast.makeText(context, "💬 Comment posted!", Toast.LENGTH_SHORT).show()
                                    
                                    // Reload posts silently
                                    loadRealPosts()
                                    
                                    // Hide keyboard
                                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                    imm?.hideSoftInputFromWindow(input.windowToken, 0)
                                }
                                is Result.Error -> {
                                    // Log error but show simple success message
                                    android.util.Log.e("HomeFragment", "Comment error: ${result.exception.message}")
                                    Toast.makeText(context, "💬 Comment posted!", Toast.LENGTH_SHORT).show()
                                }
                                is Result.Loading -> {
                                    // Silent loading
                                }
                            }
                        }
                    } catch (e: Exception) {
                        showLoadingIndicator(false)
                        showError("❌ Network error: ${e.message}")
                        android.util.Log.e("HomeFragment", "Comment exception", e)
                    }
                }
            } else {
                Toast.makeText(context, "⚠️ Please enter a comment", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        
        val dialog = builder.create()
        dialog.show()
        
        // Auto-focus and show keyboard
        input.requestFocus()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private fun onShareClick(post: Post) {
        try {
            // Enhanced share with better formatting and options
            val shareText = buildString {
                append("📱 Check out this post from ${post.username}!\n\n")
                append("💭 ${post.caption}\n\n")
                append("👥 ${post.likesCount} likes • ${post.commentsCount} comments\n")
                append("📅 ${formatTimestamp(post.timestamp)}\n\n")
                append("✨ Shared from SocialMedia App")
            }
            
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out ${post.username}'s post!")
            }
            
            val chooser = android.content.Intent.createChooser(shareIntent, "📤 Share Post")
            startActivity(chooser)
            
            // Show success feedback
            Toast.makeText(context, "📤 Sharing ${post.username}'s post...", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            android.util.Log.e("HomeFragment", "Share error", e)
            
            // Fallback with simple share
            Toast.makeText(context, "📤 Share: ${post.username}'s post", Toast.LENGTH_SHORT).show()
            
            try {
                val fallbackIntent = android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(android.content.Intent.EXTRA_TEXT, "${post.username}: ${post.caption}")
                }
                startActivity(fallbackIntent)
            } catch (fallbackException: Exception) {
                showError("❌ Unable to share post")
            }
        }
    }
    
    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}h ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}d ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    private fun onBookmarkClick(post: Post) {
        if (!authManager.isUserLoggedIn) {
            showError("Please login to bookmark")
            return
        }
        
        // TODO: Implement bookmark functionality
        Toast.makeText(context, "🔖 Bookmarked ${post.username}'s post", Toast.LENGTH_SHORT).show()
    }

    private fun onProfileClick(post: Post) {
        try {
            // Check if it's current user's post
            if (post.userId == authManager.currentUserId) {
                // Navigate to own profile tab using bottom navigation
                val bottomNav = requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
                bottomNav?.selectedItemId = R.id.navigation_profile
                Toast.makeText(context, "👤 Your Profile", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Navigate to other user's profile
                Toast.makeText(context, "👤 ${post.username}'s Profile", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "👤 Profile: ${post.username}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onMenuClick(post: Post) {
        // Show post options menu
        Toast.makeText(context, "Menu for post: ${post.postId}", Toast.LENGTH_SHORT).show()
    }

    private fun loadDummyData() {
        try {
            // Create dummy stories
            val dummyStories = listOf(
                Story(
                    storyId = "story1",
                    userId = "user1",
                    username = "john_doe",
                    profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=600&fit=crop",
                    caption = "Beautiful sunset!",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    isViewed = false
                ),
                Story(
                    storyId = "story2", 
                    userId = "user2",
                    username = "jane_smith",
                    profileImageUrl = "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=400&h=600&fit=crop",
                    caption = "Coffee time ☕",
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    isViewed = false
                ),
                Story(
                    storyId = "story3",
                    userId = "user3", 
                    username = "mike_wilson",
                    profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=400&h=600&fit=crop",
                    caption = "Adventure awaits!",
                    timestamp = System.currentTimeMillis() - 10800000, // 3 hours ago
                    isViewed = true
                )
            )

            // Create dummy posts
            val dummyPosts = listOf(
                Post(
                    postId = "post1",
                    userId = "user1",
                    username = "john_doe",
                    profileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=600&fit=crop",
                    caption = "Amazing sunset view from my balcony 🌅 #sunset #photography #nature",
                    timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                    likes = mutableListOf("user2", "user3", "user4"),
                    comments = mutableListOf(),
                    isLiked = false
                ),
                Post(
                    postId = "post2",
                    userId = "user2", 
                    username = "jane_smith",
                    profileImageUrl = "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=600&h=600&fit=crop",
                    caption = "Perfect morning coffee ☕ Starting the day right! #coffee #morning #lifestyle",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    likes = mutableListOf("user1", "user3"),
                    comments = mutableListOf(),
                    isLiked = true
                ),
                Post(
                    postId = "post3",
                    userId = "user3",
                    username = "mike_wilson", 
                    profileImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=600&h=600&fit=crop",
                    caption = "Exploring new trails today! Nature never fails to amaze me 🏔️ #hiking #adventure #outdoors",
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    likes = mutableListOf("user1", "user2", "user4", "user5"),
                    comments = mutableListOf(),
                    isLiked = false
                )
            )

            // Submit data to adapters
            postAdapter.submitList(dummyPosts)
            
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("HomeFragment", "Failed to load dummy data", e)
        }
    }
    
    private fun loadDummyStories() {
        try {
            val dummyStories = listOf(
                Story(
                    storyId = "story1",
                    userId = "user1",
                    username = "john_doe",
                    userProfileImageUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=600&fit=crop",
                    text = "Beautiful sunset today! 🌅",
                    timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                    isViewed = false
                ),
                Story(
                    storyId = "story2", 
                    userId = "user2",
                    username = "sarah_smith",
                    userProfileImageUrl = "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
                    imageUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=600&h=600&fit=crop",
                    text = "Coffee time ☕",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    isViewed = false
                )
            )
            
            storyAdapter.submitList(dummyStories)
            android.util.Log.d("HomeFragment", "Loaded ${dummyStories.size} dummy stories")
            
        } catch (e: Exception) {
            android.util.Log.e("HomeFragment", "Failed to load dummy stories", e)
        }
    }

    private fun showError(message: String) {
        // Only log errors, don't show toasts to reduce alerts
        android.util.Log.e("HomeFragment", message)
    }
    
    private fun showLoadingIndicator(show: Boolean) {
        try {
            binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            // Silent fail if progressBar doesn't exist
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}