package com.komputerkit.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.wavesoffood.R
import com.komputerkit.wavesoffood.databinding.ItemCartBinding
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.viewmodel.CartItem
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val onIncreaseClick: (ProductModel) -> Unit,
    private val onDecreaseClick: (ProductModel) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onIncreaseClick, onDecreaseClick)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onIncreaseClick: (ProductModel) -> Unit,
        private val onDecreaseClick: (ProductModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            val product = cartItem.product
            val quantity = cartItem.quantity

            binding.tvProductName.text = product.title
            binding.tvProductPrice.text = formatPrice(product.price)
            binding.tvQuantity.text = quantity.toString()
            binding.tvSubtotal.text = formatPrice(product.price * quantity)

            // Load image with Glide
            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(binding.ivProduct)

            binding.btnIncrease.setOnClickListener {
                onIncreaseClick(product)
            }

            binding.btnDecrease.setOnClickListener {
                onDecreaseClick(product)
            }
        }

        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
