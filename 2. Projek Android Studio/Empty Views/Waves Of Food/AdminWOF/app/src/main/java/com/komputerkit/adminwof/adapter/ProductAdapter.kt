package com.komputerkit.adminwof.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.adminwof.R
import com.komputerkit.adminwof.databinding.ItemProductAdminBinding
import com.komputerkit.adminwof.data.model.ProductModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter untuk RecyclerView Product List (Admin)
 * Dengan tombol Edit dan Delete
 */
class ProductAdapter(
    private val onEditClick: (ProductModel) -> Unit,
    private val onDeleteClick: (ProductModel) -> Unit
) : ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductAdminBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ProductViewHolder(
        private val binding: ItemProductAdminBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: ProductModel) {
            binding.apply {
                tvProductName.text = product.title
                tvPrice.text = formatPrice(product.price)
                tvCategory.text = product.category
                tvProductId.text = "ID: ${product.id.take(8)}..."
                
                Glide.with(root.context)
                    .load(product.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(ivProduct)
                
                btnEdit.setOnClickListener {
                    onEditClick(product)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(product)
                }
            }
        }
        
        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }
    
    private class ProductDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }
}
