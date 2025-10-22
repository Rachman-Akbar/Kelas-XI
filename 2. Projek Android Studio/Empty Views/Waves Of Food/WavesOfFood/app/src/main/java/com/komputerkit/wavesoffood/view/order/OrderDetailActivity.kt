package com.komputerkit.wavesoffood.view.order

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.adapter.OrderDetailAdapter
import com.komputerkit.wavesoffood.databinding.ActivityOrderDetailBinding
import com.komputerkit.wavesoffood.viewmodel.OrderDetailViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrderDetailBinding
    private val orderDetailViewModel: OrderDetailViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private lateinit var orderDetailAdapter: OrderDetailAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val orderId = intent.getStringExtra("ORDER_ID") ?: return
        
        setupToolbar()
        setupRecyclerView()
        observeViewModel()
        
        // Fetch order details
        orderDetailViewModel.fetchOrderDetail(orderId)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        orderDetailAdapter = OrderDetailAdapter()
        binding.rvOrderItems.adapter = orderDetailAdapter
    }
    
    private fun observeViewModel() {
        orderDetailViewModel.orderDetail.observe(this) { order ->
            order?.let {
                // Display order information
                val shortOrderId = if (it.id.length > 20) {
                    it.id.substring(0, 20) + "..."
                } else {
                    it.id
                }
                binding.tvOrderId.text = shortOrderId
                
                // Format date
                val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val dateString = it.date?.toDate()?.let { date -> dateFormat.format(date) } ?: "N/A"
                binding.tvDate.text = dateString
                
                binding.chipStatus.text = it.status
                binding.tvAddress.text = it.address
            }
        }
        
        orderDetailViewModel.orderItems.observe(this) { items ->
            orderDetailAdapter.submitList(items)
        }
        
        orderDetailViewModel.totalPrice.observe(this) { total ->
            binding.tvTotal.text = formatPrice(total)
        }
        
        orderDetailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        orderDetailViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price)
    }
}
