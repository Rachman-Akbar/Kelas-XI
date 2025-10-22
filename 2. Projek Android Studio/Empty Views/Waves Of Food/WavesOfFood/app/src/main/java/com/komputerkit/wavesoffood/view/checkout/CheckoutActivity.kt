package com.komputerkit.wavesoffood.view.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.databinding.ActivityCheckoutBinding
import com.komputerkit.wavesoffood.utils.Constants
import com.komputerkit.wavesoffood.utils.PaymentState
import com.komputerkit.wavesoffood.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private var subtotal: Double = 0.0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get subtotal from intent
        subtotal = intent.getDoubleExtra("SUBTOTAL", 0.0)
        
        setupToolbar()
        setupListeners()
        observeViewModel()
        
        // Fetch user data and calculate totals
        checkoutViewModel.fetchUserData()
        checkoutViewModel.calculateTotals(subtotal)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupListeners() {
        binding.btnPayNow.setOnClickListener {
            val address = binding.etAddress.text.toString().trim()
            
            if (address.isEmpty()) {
                binding.tilAddress.error = "Address is required"
                return@setOnClickListener
            }
            
            binding.tilAddress.error = null
            
            // Process payment with subtotal and address
            checkoutViewModel.processPayment(subtotal, address)
        }
    }
    
    private fun observeViewModel() {
        checkoutViewModel.user.observe(this) { user ->
            binding.etAddress.setText(user?.address ?: "")
        }
        
        checkoutViewModel.paymentState.observe(this) { state ->
            when (state) {
                PaymentState.Idle -> {
                    showLoading(false)
                }
                PaymentState.Processing -> {
                    showLoading(true)
                    Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show()
                }
                is PaymentState.Success -> {
                    showLoading(false)
                    navigateToOrderConfirmation(state.orderId)
                }
                is PaymentState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        
        checkoutViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
        
        checkoutViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        // Calculate and display totals
        val taxAmount = subtotal * Constants.TAX_RATE
        val totalAmount = subtotal + taxAmount
        binding.tvSubtotal.text = formatPrice(subtotal)
        binding.tvTax.text = formatPrice(taxAmount)
        binding.tvTotal.text = formatPrice(totalAmount)
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnPayNow.isEnabled = !isLoading
    }
    
    private fun navigateToOrderConfirmation(orderId: String) {
        val intent = Intent(this, OrderConfirmationActivity::class.java)
        intent.putExtra("ORDER_ID", orderId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price)
    }
}
