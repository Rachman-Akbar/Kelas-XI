package com.komputerkit.adminwof.view.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.adminwof.AdminWOFApp
import com.komputerkit.adminwof.adapter.OrderAdapter
import com.komputerkit.adminwof.databinding.ActivityOrdersManagementBinding
import com.komputerkit.adminwof.viewmodel.OrderViewModel

class OrdersManagementActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrdersManagementBinding
    private val orderViewModel: OrderViewModel by viewModels { 
        (application as AdminWOFApp).viewModelFactory 
    }
    private lateinit var orderAdapter: OrderAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        
        // Load all orders
        orderViewModel.fetchAllOrders()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            // Navigate to order detail
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            startActivity(intent)
        }
        
        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(this@OrdersManagementActivity)
            adapter = orderAdapter
        }
    }
    
    private fun observeViewModel() {
        orderViewModel.orders.observe(this) { orders ->
            if (orders.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvOrders.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvOrders.visibility = View.VISIBLE
                orderAdapter.submitList(orders)
            }
        }
        
        orderViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        orderViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh orders when returning to this activity
        orderViewModel.fetchAllOrders()
    }
}
