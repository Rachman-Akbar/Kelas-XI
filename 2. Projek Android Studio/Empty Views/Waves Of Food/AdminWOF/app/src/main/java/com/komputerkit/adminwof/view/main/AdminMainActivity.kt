package com.komputerkit.adminwof.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.adminwof.AdminWOFApp
import com.komputerkit.adminwof.R
import com.komputerkit.adminwof.adapter.ProductAdapter
import com.komputerkit.adminwof.databinding.ActivityAdminMainBinding
import com.komputerkit.adminwof.view.auth.AdminLoginActivity
import com.komputerkit.adminwof.view.order.OrdersManagementActivity
import com.komputerkit.adminwof.viewmodel.ProductViewModel

class AdminMainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminMainBinding
    private val productViewModel: ProductViewModel by viewModels { 
        (application as AdminWOFApp).viewModelFactory 
    }
    private lateinit var productAdapter: ProductAdapter
    private val auth = FirebaseAuth.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Product Management"
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        // Load all products
        productViewModel.fetchAllProducts()
    }
    
    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onEditClick = { product ->
                // TODO: Navigate to ProductFormActivity for edit
                Toast.makeText(this, "Edit: ${product.title}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product.id, product.title)
            }
        )
        
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(this@AdminMainActivity, 2)
            adapter = productAdapter
        }
    }
    
    private fun setupListeners() {
        binding.fabAddProduct.setOnClickListener {
            // TODO: Navigate to ProductFormActivity for create
            Toast.makeText(this, "Add Product - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        productViewModel.products.observe(this) { products ->
            if (products.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvProducts.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvProducts.visibility = View.VISIBLE
                productAdapter.submitList(products)
            }
        }
        
        productViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        productViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showDeleteConfirmation(productId: String, productTitle: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete '$productTitle'?")
            .setPositiveButton("Delete") { _, _ ->
                productViewModel.deleteProduct(productId)
                Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_orders -> {
                startActivity(Intent(this, OrdersManagementActivity::class.java))
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                val intent = Intent(this, AdminLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh products when returning to this activity
        productViewModel.fetchAllProducts()
    }
}
