package com.komputerkit.socialmedia.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.manager.StoryManager
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.Story
import com.komputerkit.socialmedia.databinding.FragmentHomeBinding
import com.komputerkit.socialmedia.ui.adapter.PostAdapter
import com.komputerkit.socialmedia.ui.adapter.StoryAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var storyManager: StoryManager
    private lateinit var postAdapter: PostAdapter
    private lateinit var storyAdapter: StoryAdapter

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
        setupManagers()
        setupRecyclerViews()
        loadData()
    }

    private fun setupManagers() {
        try {
            authManager = AuthManager()
            base64PostManager = Base64PostManager(authManager)
            storyManager = StoryManager(authManager)
            
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
            
            storyAdapter = StoryAdapter { story ->
                handleStoryClick(story)
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error setting up managers: ${e.message}")
        }
    }

    private fun setupRecyclerViews() {
        try {
            binding.rvStories.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = storyAdapter
            }

            binding.rvPosts.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = postAdapter
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error setting up RecyclerViews: ${e.message}")
        }
    }

    private fun loadData() {
        try {
            // Debug Firestore connection
            com.komputerkit.socialmedia.debug.FirestoreDebug.testFirestoreConnection(requireContext())
            
            loadStories()
            loadRealPosts()
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error loading data: ${e.message}")
        }
    }

    private fun loadRealPosts() {
        try {
            Log.d("HomeFragment", "Starting to load posts...")
            binding.rvPosts.visibility = View.VISIBLE
            
            // Use simple method instead of Flow for better reliability
            base64PostManager.getAllPostsSimple { result ->
                when (result) {
                    is Result.Success -> {
                        val posts = result.data
                        Log.d("HomeFragment", "Successfully loaded ${posts.size} posts")
                        
                        // Update UI on main thread
                        lifecycleScope.launch {
                            if (posts.isNotEmpty()) {
                                // Show real posts
                                Log.d("HomeFragment", "Displaying ${posts.size} real posts")
                                postAdapter.submitList(posts) {
                                    Log.d("HomeFragment", "Posts displayed in adapter")
                                    binding.rvPosts.scrollToPosition(0)
                                }
                            } else {
                                Log.d("HomeFragment", "No posts found, showing dummy data")
                                loadDummyData()
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("HomeFragment", "Error loading posts: ${result.exception.message}")
                        // Show dummy data as fallback
                        lifecycleScope.launch {
                            Log.d("HomeFragment", "Loading dummy data due to error")
                            loadDummyData()
                        }
                    }
                    is Result.Loading -> {
                        Log.d("HomeFragment", "Loading posts...")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error in loadRealPosts: ${e.message}")
            loadDummyData()
        }
    }

    private fun loadStories() {
        try {
            lifecycleScope.launch {
                storyManager.getActiveStories().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val stories = result.data
                            Log.d("HomeFragment", "Loaded ${stories.size} stories")
                            if (stories.isNotEmpty()) {
                                storyAdapter.submitList(stories)
                                binding.rvStories.visibility = View.VISIBLE
                            } else {
                                Log.d("HomeFragment", "No real stories found, loading dummy stories")
                                loadDummyStories() 
                            }
                        }
                        is Result.Error -> {
                            Log.e("HomeFragment", "Error loading stories: ${result.exception.message}")
                            // Show dummy stories if error occurs
                            loadDummyStories()
                        }
                        is Result.Loading -> {
                            Log.d("HomeFragment", "Loading stories...")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error in loadStories: ${e.message}")
            loadDummyStories()
        }
    }

    private fun handleStoryClick(story: Story) {
        Log.d("HomeFragment", "Story clicked: ${story.username}")
        try {
            val intent = android.content.Intent(requireContext(), com.komputerkit.socialmedia.ui.story.StoryViewerActivity::class.java)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_STORY_ID, story.storyId)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_USERNAME, story.username)
            intent.putExtra(com.komputerkit.socialmedia.ui.story.StoryViewerActivity.EXTRA_STORY_INDEX, 0)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error opening story: ${e.message}")
            android.widget.Toast.makeText(context, "Cannot open story", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLikeClick(post: Post) {
        if (!authManager.isUserLoggedIn) {
            Log.d("HomeFragment", "User not logged in for like action")
            android.widget.Toast.makeText(context, "Please login to like posts", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                base64PostManager.likePost(post.postId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            Log.d("HomeFragment", "Post liked successfully")
                            // Refresh posts to show updated like count
                            loadRealPosts()
                        }
                        is Result.Error -> {
                            Log.e("HomeFragment", "Error liking post: ${result.exception.message}")
                            android.widget.Toast.makeText(context, "Failed to like post", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> {
                            // Show loading if needed
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error in handleLikeClick: ${e.message}")
                android.widget.Toast.makeText(context, "Error liking post", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleCommentClick(post: Post) {
        Log.d("HomeFragment", "Comment clicked for post: ${post.username}")
    }

    private fun handleShareClick(post: Post) {
        Log.d("HomeFragment", "Share clicked for post: ${post.username}")
    }

    private fun handleBookmarkClick(post: Post) {
        Log.d("HomeFragment", "Bookmark clicked for post: ${post.username}")
    }

    private fun handleProfileClick(post: Post) {
        Log.d("HomeFragment", "Profile clicked for user: ${post.username}")
    }

    private fun handleMenuClick(post: Post) {
        Log.d("HomeFragment", "Menu clicked for post: ${post.postId}")
    }

    private fun loadDummyData() {
        val dummyPosts = listOf(
            Post(
                postId = "dummy1",
                userId = "user1", 
                username = "john_doe",
                profileImageUrl = "https://via.placeholder.com/150",
                imageUrl = "https://via.placeholder.com/400",
                caption = "Beautiful sunset today! 🌅",
                timestamp = System.currentTimeMillis(),
                likes = mutableListOf("user2", "user3"),
                comments = mutableListOf(),
                isLiked = false
            )
        )
        postAdapter.submitList(dummyPosts)
    }

    private fun loadDummyStories() {
        val dummyStories = listOf(
            Story(
                storyId = "story1",
                userId = "user1",
                username = "john_doe",
                userProfileImageUrl = "https://via.placeholder.com/150",
                imageUrl = "https://via.placeholder.com/400", 
                text = "My story",
                timestamp = System.currentTimeMillis(),
                isViewed = false
            )
        )
        storyAdapter.submitList(dummyStories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
