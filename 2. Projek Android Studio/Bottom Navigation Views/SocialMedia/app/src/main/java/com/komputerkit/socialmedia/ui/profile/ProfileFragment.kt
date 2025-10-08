package com.komputerkit.socialmedia.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.socialmedia.ui.auth.LoginActivity
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.databinding.FragmentProfileBinding
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.ui.postdetail.PostDetailActivity
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var profilePostAdapter: ProfilePostAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupManagers()
        setupViews()
        loadUserData()
        loadUserPosts()
    }

    private fun setupManagers() {
        try {
            authManager = AuthManager()
            base64PostManager = Base64PostManager(authManager)
            auth = FirebaseAuth.getInstance()
            
            profilePostAdapter = ProfilePostAdapter(requireContext()) { post ->
                Log.d("ProfileFragment", "Post clicked: ${post.postId}")
                // Navigate to post detail
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra("POST_ID", post.postId)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error setting up managers: ${e.message}")
        }
    }

    private fun setupViews() {
        try {
            binding.rvUserPosts.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = profilePostAdapter
                visibility = View.VISIBLE // Ensure it's visible from start
            }
            
            Log.d("ProfileFragment", "RecyclerView setup completed - LayoutManager: ${binding.rvUserPosts.layoutManager}, Adapter: ${binding.rvUserPosts.adapter}")

            binding.btnLogout.setOnClickListener {
                logout()
            }

            binding.btnSettings.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Debug Options")
                    .setMessage("Test grid functionality")
                    .setPositiveButton("Create Sample Posts") { _, _ ->
                        showDebugOptions()
                    }
                    .setNegativeButton("Test Direct Load") { _, _ ->
                        testDirectPostLoad()
                    }
                    .setNeutralButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            binding.btnEditProfile.setOnClickListener {
                Log.d("ProfileFragment", "Edit profile clicked")
                
                // Debug: Create sample post for testing
                val currentUser = authManager.currentUser
                if (currentUser != null) {
                    Log.d("ProfileFragment", "Creating sample post for debugging...")
                    com.komputerkit.socialmedia.debug.FirestoreDebug.createSamplePost(
                        currentUser.uid,
                        currentUser.displayName ?: currentUser.email ?: "Test User"
                    )
                    
                    // Refresh posts after creating sample
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(2000) // Wait 2 seconds for Firestore
                        Log.d("ProfileFragment", "Refreshing posts after creating sample...")
                        loadUserPosts()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error setting up views: ${e.message}")
        }
    }

    private fun loadUserData() {
        try {
            val currentUser = authManager.currentUser
            if (currentUser != null) {
                Log.d("ProfileFragment", "Loading user data for: ${currentUser.uid}")
                
                // Set basic info immediately from Firebase Auth
                val displayName = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User"
                val email = currentUser.email ?: ""
                val userId = "@${currentUser.email?.substringBefore("@") ?: "user"}"
                
                Log.d("ProfileFragment", "Firebase Auth data - Name: $displayName, Email: $email")
                
                // Update UI immediately with Instagram-like style
                binding.apply {
                    // Header username
                    tvHeaderUsername.text = displayName
                    
                    // Profile section
                    tvUsername.text = displayName
                    tvUserId.text = userId
                    
                    // Set a nice bio
                    tvBio.text = "Welcome to my profile! 📸✨\nSharing moments and memories"
                    
                    // Initialize counts (will be updated later)
                    tvPostsCount.text = "0"
                    tvFollowersCount.text = "0"
                    tvFollowingCount.text = "0"
                }
                
                // Load profile image
                val photoUrl = currentUser.photoUrl?.toString()
                if (!photoUrl.isNullOrEmpty()) {
                    loadProfileImageFromUrl(photoUrl)
                } else {
                    loadDefaultProfileImage()
                }
                
                // Try to load additional data from Firestore (optional)
                loadFirestoreUserData(currentUser)
                
                // Load user stats
                loadUserStats(currentUser.uid)
                
            } else {
                Log.e("ProfileFragment", "Current user is null")
                navigateToLogin()
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading user data: ${e.message}")
        }
    }

    private fun loadUserStats(userId: String) {
        try {
            Log.d("ProfileFragment", "Loading user stats for: $userId")
            
            // Load posts count
            lifecycleScope.launch {
                base64PostManager.getUserPosts(userId) { result ->
                    when (result) {
                        is Result.Success -> {
                            val postsCount = result.data.size
                            binding.tvPostsCount.text = postsCount.toString()
                            Log.d("ProfileFragment", "Updated posts count: $postsCount")
                        }
                        else -> {
                            binding.tvPostsCount.text = "0"
                        }
                    }
                }
            }
            
            // For now, set followers and following to 0 (can be enhanced later)
            binding.tvFollowersCount.text = "0"
            binding.tvFollowingCount.text = "0"
            
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading user stats: ${e.message}")
        }
    }
    
    private fun loadFirestoreUserData(currentUser: com.google.firebase.auth.FirebaseUser) {
        lifecycleScope.launch {
            try {
                when (val result = authManager.getCurrentUserData()) {
                    is Result.Success -> {
                        val userData = result.data
                        Log.d("ProfileFragment", "Firestore user data: ${userData.username}, ${userData.email}")
                        
                        // Update UI with Firestore data if available
                        if (userData.username.isNotEmpty()) {
                            binding.tvUsername.text = userData.username
                        }
                        
                        // Load profile image from Firestore if available
                        if (userData.profileImageBase64.isNotEmpty()) {
                            loadProfileImageFromBase64(userData.profileImageBase64)
                        } else if (userData.profileImageUrl.isNotEmpty()) {
                            loadProfileImageFromUrl(userData.profileImageUrl)
                        }
                    }
                    is Result.Error -> {
                        Log.w("ProfileFragment", "Could not load Firestore user data: ${result.exception.message}")
                        // Continue with Firebase Auth data
                    }
                    is Result.Loading -> {
                        Log.d("ProfileFragment", "Loading Firestore user data...")
                    }
                }
            } catch (e: Exception) {
                Log.w("ProfileFragment", "Exception loading Firestore user data: ${e.message}")
            }
        }
    }

    private fun loadProfileImageFromBase64(base64Image: String) {
        try {
            Log.d("ProfileFragment", "Loading profile image from Base64, length: ${base64Image.length}")
            val imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (bitmap != null) {
                Glide.with(this)
                    .load(bitmap)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(binding.ivProfilePicture)
            } else {
                Log.e("ProfileFragment", "Failed to decode Base64 image")
                loadDefaultProfileImage()
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading Base64 profile image: ${e.message}")
            loadDefaultProfileImage()
        }
    }
    
    private fun loadProfileImageFromUrl(imageUrl: String) {
        try {
            Log.d("ProfileFragment", "Loading profile image from URL: $imageUrl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(binding.ivProfilePicture)
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading URL profile image: ${e.message}")
            loadDefaultProfileImage()
        }
    }

    private fun loadDefaultProfileImage() {
        try {
            binding.ivProfilePicture.setImageResource(R.drawable.ic_profile_placeholder)
            Log.d("ProfileFragment", "Loaded default profile image")
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading default profile image: ${e.message}")
        }
    }

    private fun loadUserPosts() {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d("ProfileFragment", "Loading posts for user: ${currentUser.uid}")
                
                base64PostManager.getUserPosts(currentUser.uid) { result ->
                    activity?.runOnUiThread {
                        when (result) {
                            is Result.Success -> {
                                val posts = result.data
                                Log.d("ProfileFragment", "Received ${posts.size} posts for current user")
                                
                                if (posts.isNotEmpty()) {
                                    Log.d("ProfileFragment", "Submitting ${posts.size} posts to adapter")
                                    profilePostAdapter.submitList(posts)
                                    binding.rvUserPosts.visibility = View.VISIBLE
                                } else {
                                    Log.w("ProfileFragment", "No posts found for current user")
                                    Toast.makeText(context, "No posts yet. Create some posts!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            is Result.Error -> {
                                Log.e("ProfileFragment", "Error loading posts: ${result.exception.message}")
                                Toast.makeText(context, "Error loading posts: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                            }
                            is Result.Loading -> {
                                Log.d("ProfileFragment", "Loading posts...")
                            }
                        }
                    }
                }
            } else {
                Log.e("ProfileFragment", "Current user is null")
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading user posts: ${e.message}")
        }
    }

    private fun logout() {
        try {
            authManager.logout()
            navigateToLogin()
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error during logout: ${e.message}")
        }
    }

    private fun navigateToLogin() {
        try {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error navigating to login: ${e.message}")
        }
    }

    private fun createSamplePostForUser() {
        createSamplePostForUser("Sample Post", "This is a sample post content")
    }
    
    private fun createSamplePostForUser(title: String, content: String) {
        try {
            val currentUser = authManager.currentUser
            if (currentUser != null) {
                Log.d("ProfileFragment", "Creating sample post for user...")
                
                lifecycleScope.launch {
                    // Create a simple sample post using Base64PostManager
                    val sampleImageBase64 = createSampleBase64Image()
                    base64PostManager.uploadPostWithBase64(
                        sampleImageBase64,
                        "$title - $content"
                    ).collect { result ->
                        when (result) {
                            is Result.Success -> {
                                Log.d("ProfileFragment", "Sample post created successfully")
                                // Refresh posts after creating
                                kotlinx.coroutines.delay(1000)
                                loadUserPosts()
                            }
                            is Result.Error -> {
                                Log.e("ProfileFragment", "Error creating sample post: ${result.exception.message}")
                            }
                            is Result.Loading -> {
                                Log.d("ProfileFragment", "Creating sample post...")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error creating sample post: ${e.message}")
        }
    }
    
    private fun createSampleBase64Image(): String {
        // Create a simple colored bitmap and convert to base64
        return try {
            val bitmap = android.graphics.Bitmap.createBitmap(300, 300, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            val paint = android.graphics.Paint()
            paint.color = android.graphics.Color.parseColor("#4CAF50")
            canvas.drawRect(0f, 0f, 300f, 300f, paint)
            
            // Add some text
            paint.color = android.graphics.Color.WHITE
            paint.textSize = 24f
            paint.textAlign = android.graphics.Paint.Align.CENTER
            canvas.drawText("Sample Post", 150f, 150f, paint)
            
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error creating sample image: ${e.message}")
            ""
        }
    }
    
    private fun showDebugOptions() {
        try {
            val currentUser = authManager.currentUser
            if (currentUser != null) {
                // Create multiple sample posts for better testing
                Log.d("ProfileFragment", "Creating multiple sample posts for testing...")
                
                lifecycleScope.launch {
                    repeat(3) { index ->
                        val sampleImageBase64 = createSampleBase64Image()
                        base64PostManager.uploadPostWithBase64(
                            sampleImageBase64,
                            "${currentUser.displayName ?: currentUser.email ?: "Test User"} - Post ${index + 1}"
                        ).collect { result ->
                            when (result) {
                                is Result.Success -> {
                                    Log.d("ProfileFragment", "Sample post ${index + 1} created")
                                }
                                is Result.Error -> {
                                    Log.e("ProfileFragment", "Error creating sample post ${index + 1}: ${result.exception.message}")
                                }
                                else -> { /* Loading */ }
                            }
                        }
                        kotlinx.coroutines.delay(500) // Small delay between posts
                    }
                    
                    // Refresh data after creating posts
                    kotlinx.coroutines.delay(2000) // Wait for Firestore
                    Log.d("ProfileFragment", "Refreshing data after creating sample posts...")
                    loadUserData()
                    loadUserPosts()
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error in showDebugOptions: ${e.message}")
        }
    }

    private fun testDirectPostLoad() {
        Log.d("ProfileFragment", "Testing direct post loading...")
        val currentUserId = auth.currentUser?.uid ?: return
        
        base64PostManager.getUserPosts(currentUserId) { result ->
            activity?.runOnUiThread {
                when (result) {
                    is Result.Success -> {
                        val posts = result.data
                        Log.d("ProfileFragment", "Direct test - Found ${posts.size} posts")
                        posts.forEachIndexed { index, post ->
                            Log.d("ProfileFragment", "Post $index: ID=${post.postId}, imageBase64 length=${post.imageBase64?.length ?: 0}")
                        }
                        
                        if (posts.isNotEmpty()) {
                            profilePostAdapter.submitList(posts)
                            binding.rvUserPosts.visibility = View.VISIBLE
                            Toast.makeText(context, "Direct load: ${posts.size} posts", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Direct load: No posts found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Result.Error -> {
                        Log.e("ProfileFragment", "Direct test error: ${result.exception.message}")
                        Toast.makeText(context, "Error: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        Log.d("ProfileFragment", "Direct test loading...")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            Log.d("ProfileFragment", "Fragment resumed, refreshing data...")
            loadUserData()
            loadUserPosts()
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error in onResume: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
