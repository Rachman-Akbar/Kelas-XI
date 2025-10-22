package com.komputerkit.wavesoffood.view.favorites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.adapter.ProductAdapter
import com.komputerkit.wavesoffood.databinding.ActivityFavoritesBinding
import com.komputerkit.wavesoffood.view.product.ProductDetailActivity
import com.komputerkit.wavesoffood.viewmodel.FavoritesViewModel

class FavoritesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityFavoritesBinding
    private val favoritesViewModel: FavoritesViewModel by viewModels { 
        (application as WavesOfFoodApp).viewModelFactory 
    }
    private lateinit var productAdapter: ProductAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        
        // Load favorites
        favoritesViewModel.fetchFavoriteProducts()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Navigate to product detail
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }
        
        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            adapter = productAdapter
        }
    }
    
    private fun observeViewModel() {
        favoritesViewModel.favoriteProducts.observe(this) { products ->
            if (products.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
                productAdapter.submitList(products)
            }
        }
        
        favoritesViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        favoritesViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh favorites when returning to this activity
        favoritesViewModel.fetchFavoriteProducts()
    }
}
