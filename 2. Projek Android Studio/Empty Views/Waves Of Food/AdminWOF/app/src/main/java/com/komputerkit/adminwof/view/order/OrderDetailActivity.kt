package com.komputerkit.adminwof.view.order

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.adminwof.AdminWOFApp
import com.komputerkit.adminwof.R
import com.komputerkit.adminwof.adapter.OrderItemAdapter
import com.komputerkit.adminwof.databinding.ActivityOrderDetailBinding
import com.komputerkit.adminwof.utils.Constants
import com.komputerkit.adminwof.utils.UiState
import com.komputerkit.adminwof.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrderDetailBinding
    private val orderViewModel: OrderViewModel by viewModels { 
        (application as AdminWOFApp).viewModelFactory 
    }
    private lateinit var orderItemAdapter: OrderItemAdapter
    private var orderId: String = ""
    private var selectedStatus: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        orderId = intent.getStringExtra("ORDER_ID") ?: return
        
        setupToolbar()
        setupRecyclerView()
        setupStatusSpinner()
        setupListeners()
        observeViewModel()
        
        // Load order detail
        orderViewModel.fetchOrderDetail(orderId)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        
        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            adapter = orderItemAdapter
        }
    }
    
    private fun setupStatusSpinner() {
        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Constants.ORDER_STATUSES
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerStatus.adapter = statusAdapter
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedStatus = Constants.ORDER_STATUSES[position]
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnSaveChanges.setOnClickListener {
            if (selectedStatus.isNotEmpty()) {
                orderViewModel.updateOrderStatus(orderId, selectedStatus)
            } else {
                Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        // Observe order data
        orderViewModel.order.observe(this) { order ->
            order?.let {
                binding.tvOrderId.text = "Order #${it.id.take(8)}"
                binding.tvOrderDate.text = formatDate(it.date?.toDate()?.time ?: 0)
                
                // Set current status in spinner
                val currentStatusIndex = Constants.ORDER_STATUSES.indexOf(it.status)
                if (currentStatusIndex >= 0) {
                    binding.spinnerStatus.setSelection(currentStatusIndex)
                }
            }
        }
        
        // Observe user data
        orderViewModel.user.observe(this) { user ->
            user?.let {
                binding.tvUserName.text = "Name: ${it.name}"
                binding.tvUserEmail.text = "Email: ${it.email}"
                binding.tvUserAddress.text = "Address: ${it.address}"
            }
        }
        
        // Observe order items
        orderViewModel.orderItems.observe(this) { items ->
            orderItemAdapter.submitList(items)
        }
        
        // Observe total price
        orderViewModel.totalPrice.observe(this) { total ->
            binding.tvTotalPrice.text = formatPrice(total)
        }
        
        // Observe loading state
        orderViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // Observe error
        orderViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
        
        // Observe UI state for status update
        orderViewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnSaveChanges.isEnabled = false
                    binding.btnSaveChanges.text = "Updating..."
                }
                is UiState.Success -> {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                    Toast.makeText(this, state.data, Toast.LENGTH_LONG).show()
                    orderViewModel.resetUiState()
                    // Close activity and return to list
                    finish()
                }
                is UiState.Error -> {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    orderViewModel.resetUiState()
                }
                is UiState.Idle -> {
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.text = "Save Changes"
                }
            }
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(timestamp)
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price)
    }
}
