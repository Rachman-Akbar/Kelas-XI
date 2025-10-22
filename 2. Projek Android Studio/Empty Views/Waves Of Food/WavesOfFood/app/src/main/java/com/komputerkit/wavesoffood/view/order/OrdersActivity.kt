package com.komputerkit.wavesoffood.view.order

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.adapter.OrderAdapter
import com.komputerkit.wavesoffood.databinding.ActivityOrdersBinding
import com.komputerkit.wavesoffood.viewmodel.OrdersViewModel

class OrdersActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrdersBinding
    private val ordersViewModel: OrdersViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private lateinit var orderAdapter: OrderAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        
        // Fetch user orders
        ordersViewModel.fetchOrders()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            // Navigate to OrderDetailActivity
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            startActivity(intent)
        }
        
        binding.rvOrders.adapter = orderAdapter
    }
    
    private fun observeViewModel() {
        ordersViewModel.orders.observe(this) { orders ->
            orderAdapter.submitList(orders)
            
            // Show/hide empty state
            if (orders.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvOrders.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvOrders.visibility = View.VISIBLE
            }
        }
        
        ordersViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        ordersViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
}
