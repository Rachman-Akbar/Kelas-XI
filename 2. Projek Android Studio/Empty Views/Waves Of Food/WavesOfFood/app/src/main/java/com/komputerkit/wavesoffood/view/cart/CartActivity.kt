package com.komputerkit.wavesoffood.view.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.adapter.CartAdapter
import com.komputerkit.wavesoffood.databinding.ActivityCartBinding
import com.komputerkit.wavesoffood.viewmodel.CartItem
import com.komputerkit.wavesoffood.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private lateinit var cartAdapter: CartAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        // Load cart items
        cartViewModel.fetchCartItems()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseClick = { product ->
                cartViewModel.addToCart(product.id)
            },
            onDecreaseClick = { product ->
                cartViewModel.removeFromCart(product.id)
            }
        )
        
        binding.rvCartItems.adapter = cartAdapter
    }
    
    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            if (cartViewModel.cartItems.value?.isNotEmpty() == true) {
                showCheckoutDialog()
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        cartViewModel.cartItems.observe(this) { cartMap ->
            val cartItems = cartMap.map { (product, quantity) ->
                CartItem(product, quantity)
            }
            
            cartAdapter.submitList(cartItems)
            
            // Update UI based on cart state
            if (cartItems.isEmpty()) {
                binding.layoutEmptyState.visibility = View.VISIBLE
                binding.rvCartItems.visibility = View.GONE
                binding.cardCheckout.visibility = View.GONE
            } else {
                binding.layoutEmptyState.visibility = View.GONE
                binding.rvCartItems.visibility = View.VISIBLE
                binding.cardCheckout.visibility = View.VISIBLE
            }
        }
        
        cartViewModel.totalPrice.observe(this) { total ->
            binding.tvTotalPrice.text = formatPrice(total)
        }
        
        cartViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        cartViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showCheckoutDialog() {
        val subtotal = cartViewModel.totalPrice.value ?: 0.0
        
        // Navigate to CheckoutActivity
        val intent = Intent(this, com.komputerkit.wavesoffood.view.checkout.CheckoutActivity::class.java)
        intent.putExtra("SUBTOTAL", subtotal)
        startActivity(intent)
    }
    
    private fun processCheckout() {
        // This method is no longer used - checkout handled in CheckoutActivity
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price)
    }
}
