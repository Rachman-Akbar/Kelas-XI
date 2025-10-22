package com.komputerkit.wavesoffood.view.product

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.komputerkit.wavesoffood.R
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.databinding.ActivityProductDetailBinding
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.viewmodel.CartViewModel
import com.komputerkit.wavesoffood.viewmodel.FavoritesViewModel
import com.komputerkit.wavesoffood.viewmodel.ProductDetailViewModel
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProductDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private val cartViewModel: CartViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private val favoritesViewModel: FavoritesViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    private var currentProduct: ProductModel? = null
    private var isFavorite: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val productId = intent.getStringExtra("PRODUCT_ID") ?: return
        
        setupListeners()
        observeViewModel()
        productDetailViewModel.fetchProduct(productId)
    }
    
    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnAddToCart.setOnClickListener {
            currentProduct?.let { product ->
                cartViewModel.addToCart(product.id)
                Toast.makeText(this, "${product.title} added to cart", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnFavorite.setOnClickListener {
            currentProduct?.let { product ->
                favoritesViewModel.toggleFavorite(product.id, isFavorite)
            }
        }
    }
    
    private fun observeViewModel() {
        productDetailViewModel.product.observe(this) { product ->
            product?.let {
                currentProduct = it
                displayProduct(it)
                // Check favorite status
                favoritesViewModel.checkIsFavorite(it.id)
            }
        }
        
        productDetailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        productDetailViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        // Observe favorite status
        favoritesViewModel.isFavorite.observe(this) { favorite ->
            isFavorite = favorite
            updateFavoriteIcon(favorite)
        }
        
        favoritesViewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateFavoriteIcon(isFav: Boolean) {
        binding.btnFavorite.setImageResource(
            if (isFav) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }
    
    private fun displayProduct(product: ProductModel) {
        binding.apply {
            tvProductName.text = product.title
            tvCategory.text = product.category
            tvProductPrice.text = formatPrice(product.price)
            tvDescription.text = product.description
            
            Glide.with(this@ProductDetailActivity)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(ivProduct)
        }
    }
    
    private fun formatPrice(price: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(price)
    }
}
