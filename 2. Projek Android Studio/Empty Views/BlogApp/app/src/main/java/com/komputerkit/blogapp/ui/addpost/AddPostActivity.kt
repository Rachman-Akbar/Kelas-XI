package com.komputerkit.blogapp.ui.addpost

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.komputerkit.blogapp.R
import com.komputerkit.blogapp.databinding.ActivityAddPostBinding
import kotlinx.coroutines.launch

class AddPostActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddPostBinding
    private val viewModel: AddPostViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        observeViewModel()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Tulis Blog"
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.addPostState.collect { state ->
                when (state) {
                    is AddPostViewModel.AddPostState.Loading -> {
                        // Disable menu item saat loading
                        invalidateOptionsMenu()
                    }
                    is AddPostViewModel.AddPostState.Success -> {
                        Toast.makeText(this@AddPostActivity, "Blog berhasil dipublish!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is AddPostViewModel.AddPostState.Error -> {
                        Toast.makeText(this@AddPostActivity, state.message, Toast.LENGTH_LONG).show()
                        invalidateOptionsMenu()
                    }
                    is AddPostViewModel.AddPostState.Idle -> {
                        invalidateOptionsMenu()
                    }
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_post, menu)
        
        // Disable publish button jika sedang loading
        val publishItem = menu.findItem(R.id.action_publish)
        publishItem.isEnabled = viewModel.addPostState.value !is AddPostViewModel.AddPostState.Loading
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_publish -> {
                publishPost()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun publishPost() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        
        if (validateInput(title, content)) {
            viewModel.publishPost(title, content)
        }
    }
    
    private fun validateInput(title: String, content: String): Boolean {
        if (title.isEmpty()) {
            binding.tilTitle.error = "Judul tidak boleh kosong"
            return false
        } else {
            binding.tilTitle.error = null
        }
        
        if (content.isEmpty()) {
            binding.tilContent.error = "Konten tidak boleh kosong"
            return false
        } else {
            binding.tilContent.error = null
        }
        
        return true
    }
}