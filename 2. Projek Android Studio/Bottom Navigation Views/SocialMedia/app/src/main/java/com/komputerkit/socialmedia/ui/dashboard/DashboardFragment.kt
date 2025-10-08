package com.komputerkit.socialmedia.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.manager.Base64PostManager
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.databinding.FragmentDashboardBinding
import com.komputerkit.socialmedia.ui.adapter.GridPostAdapter
import com.komputerkit.socialmedia.ui.postdetail.PostDetailActivity
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authManager: AuthManager
    private lateinit var base64PostManager: Base64PostManager
    private lateinit var gridPostAdapter: GridPostAdapter
    private var allPosts = mutableListOf<Post>()
    private var filteredPosts = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupManagers()
            setupRecyclerView()
            setupSearchView()
            loadPostsFromDatabase()
        } catch (e: Exception) {
            e.printStackTrace() 
            showError("Error initializing search screen: ${e.message}")
        }
    }
    
    private fun setupManagers() {
        try {
            authManager = AuthManager()
            base64PostManager = Base64PostManager(authManager)
            
            gridPostAdapter = GridPostAdapter { post ->
                // Navigate to post detail when grid item is clicked
                val intent = PostDetailActivity.newIntent(requireContext(), post.postId)
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error setting up managers: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        binding.rvExplore.apply {
            layoutManager = GridLayoutManager(context, 3) // 3 columns grid
            adapter = gridPostAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchPosts(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Real-time search implementation
                if (newText.isNullOrBlank()) {
                    // Show all posts if search is empty
                    filteredPosts.clear()
                    filteredPosts.addAll(allPosts)
                    gridPostAdapter.submitList(filteredPosts.toList())
                } else {
                    searchPosts(newText)
                }
                return true
            }
        })
    }

    private fun loadPostsFromDatabase() {
        try {
            binding.progressBar?.visibility = View.VISIBLE
            
            lifecycleScope.launch {
                base64PostManager.getAllPosts().collect { result ->
                    when (result) {
                        is Result.Success -> {
                            allPosts.clear()
                            allPosts.addAll(result.data.shuffled()) // Randomize posts
                            filteredPosts.clear()
                            filteredPosts.addAll(allPosts)
                            
                            gridPostAdapter.submitList(filteredPosts.toList())
                            
                            if (allPosts.isEmpty()) {
                                showMessage("No posts found. Users can upload posts to see them here.")
                            }
                            
                            Log.d("DashboardFragment", "Loaded ${allPosts.size} posts from database in random order")
                            binding.progressBar?.visibility = View.GONE
                        }
                        is Result.Error -> {
                            showError("Failed to load posts: ${result.exception.message}")
                            Log.e("DashboardFragment", "Error loading posts", result.exception)
                            binding.progressBar?.visibility = View.GONE
                        }
                        is Result.Loading -> {
                            binding.progressBar?.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } catch (e: Exception) {
            binding.progressBar?.visibility = View.GONE
            showError("Unexpected error: ${e.message}")
            Log.e("DashboardFragment", "Unexpected error loading posts", e)
        }
    }

    private fun searchPosts(query: String) {
        val searchQuery = query.lowercase().trim()
        
        filteredPosts.clear()
        filteredPosts.addAll(allPosts.filter { post ->
            post.username.lowercase().contains(searchQuery) ||
            post.caption.lowercase().contains(searchQuery)
        })
        
        gridPostAdapter.submitList(filteredPosts.toList())
        
        if (filteredPosts.isEmpty() && searchQuery.isNotEmpty()) {
            showMessage("No posts found for '$query'")
        }
        
        Log.d("DashboardFragment", "Search for '$query' returned ${filteredPosts.size} results")
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh posts when returning to this fragment
        loadPostsFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}