package com.komputerkit.wavesoffood

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.wavesoffood.adapter.ProductAdapter
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.databinding.ActivityMainBinding
import com.komputerkit.wavesoffood.view.auth.AuthActivity
import com.komputerkit.wavesoffood.view.cart.CartActivity
import com.komputerkit.wavesoffood.view.product.ProductDetailActivity
import com.komputerkit.wavesoffood.view.profile.ProfileActivity
import com.komputerkit.wavesoffood.viewmodel.CartViewModel
import com.komputerkit.wavesoffood.viewmodel.HomeViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels {
        (application as WavesOfFoodApp).viewModelFactory
    }
    private val cartViewModel: CartViewModel by viewModels {
        (application as WavesOfFoodApp).viewModelFactory
    }
    private lateinit var productAdapter: ProductAdapter
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        // Load products
        homeViewModel.fetchProducts()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_logout -> {
                auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            },
            onAddToCart = { product ->
                cartViewModel.addToCart(product.id)
                Toast.makeText(this, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }
    }
    
    private fun setupListeners() {
        binding.fabCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipAll -> homeViewModel.fetchProducts()
                    R.id.chipFood -> homeViewModel.fetchProductsByCategory("Food")
                    R.id.chipDrink -> homeViewModel.fetchProductsByCategory("Drink")
                    R.id.chipSnack -> homeViewModel.fetchProductsByCategory("Snack")
                }
            }
        }
    }
    
    private fun observeViewModel() {
        homeViewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
            binding.tvEmptyState.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        }
        
        homeViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        homeViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}