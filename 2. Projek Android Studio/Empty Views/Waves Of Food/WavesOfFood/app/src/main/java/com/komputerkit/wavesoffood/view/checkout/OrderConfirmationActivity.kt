package com.komputerkit.wavesoffood.view.checkout

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.MainActivity
import com.komputerkit.wavesoffood.databinding.ActivityOrderConfirmationBinding
import com.komputerkit.wavesoffood.view.order.OrdersActivity

class OrderConfirmationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOrderConfirmationBinding
    private var orderId: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        orderId = intent.getStringExtra("ORDER_ID") ?: ""
        
        displayOrderInfo()
        setupListeners()
    }
    
    private fun displayOrderInfo() {
        // Display shortened order ID
        val shortOrderId = if (orderId.length > 12) {
            orderId.substring(0, 12) + "..."
        } else {
            orderId
        }
        binding.tvOrderId.text = shortOrderId
        binding.tvStatus.text = "Processing"
    }
    
    private fun setupListeners() {
        binding.btnViewOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        
        binding.btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    
    override fun onBackPressed() {
        // Prevent back navigation - force user to use buttons
        // This prevents going back to checkout
    }
}
