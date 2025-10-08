package com.komputerkit.blogapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.blogapp.R
import com.komputerkit.blogapp.databinding.ActivityHomeBinding
import com.komputerkit.blogapp.service.AuthService
import com.komputerkit.blogapp.ui.addpost.AddPostActivity
import com.komputerkit.blogapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var blogAdapter: BlogAdapter
    private val authService = AuthService()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "BlogApp"
    }
    
    private fun setupRecyclerView() {
        blogAdapter = BlogAdapter { blog ->
            // Handle blog click - bisa navigate ke detail
            // Untuk saat ini kita skip implementasi detail
        }
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = blogAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshBlogs()
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.blogs.collect { blogs ->
                blogAdapter.submitList(blogs)
                binding.swipeRefresh.isRefreshing = false
            }
        }
        
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                // Handle loading state jika diperlukan
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun logout() {
        authService.logoutUser()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data ketika kembali ke activity ini
        viewModel.refreshBlogs()
    }
}