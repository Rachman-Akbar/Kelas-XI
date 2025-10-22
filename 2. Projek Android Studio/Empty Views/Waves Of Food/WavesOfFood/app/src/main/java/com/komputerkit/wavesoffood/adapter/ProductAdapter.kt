package com.komputerkit.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.wavesoffood.R
import com.komputerkit.wavesoffood.databinding.ItemProductBinding
import com.komputerkit.wavesoffood.data.model.ProductModel
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val onItemClick: (ProductModel) -> Unit,
    private val onAddToCart: (ProductModel) -> Unit
) : ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onItemClick, onAddToCart)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onItemClick: (ProductModel) -> Unit,
        private val onAddToCart: (ProductModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) {
            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = formatPrice(product.price)

            // Load image with Glide
            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(binding.ivProduct)

            binding.root.setOnClickListener {
                onItemClick(product)
            }

            binding.btnAddToCart.setOnClickListener {
                onAddToCart(product)
            }
        }

        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }
}
