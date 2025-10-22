package com.komputerkit.adminwof.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.komputerkit.adminwof.databinding.ItemOrderItemBinding
import com.komputerkit.adminwof.viewmodel.OrderItemDetail
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter untuk Order Items di Order Detail
 */
class OrderItemAdapter : ListAdapter<OrderItemDetail, OrderItemAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class OrderItemViewHolder(
        private val binding: ItemOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: OrderItemDetail) {
            binding.apply {
                tvProductName.text = item.product.title
                tvQuantity.text = "x${item.quantity}"
                tvProductPrice.text = formatPrice(item.product.price)
                tvSubtotal.text = formatPrice(item.product.price * item.quantity)
            }
        }
        
        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }
    
    private class OrderItemDiffCallback : DiffUtil.ItemCallback<OrderItemDetail>() {
        override fun areItemsTheSame(oldItem: OrderItemDetail, newItem: OrderItemDetail): Boolean {
            return oldItem.product.id == newItem.product.id
        }
        
        override fun areContentsTheSame(oldItem: OrderItemDetail, newItem: OrderItemDetail): Boolean {
            return oldItem == newItem
        }
    }
}
