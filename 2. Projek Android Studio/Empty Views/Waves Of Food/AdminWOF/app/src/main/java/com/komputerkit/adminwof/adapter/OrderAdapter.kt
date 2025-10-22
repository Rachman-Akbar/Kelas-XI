package com.komputerkit.adminwof.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.komputerkit.adminwof.databinding.ItemOrderBinding
import com.komputerkit.adminwof.data.model.OrderModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter untuk RecyclerView Order List
 */
class OrderAdapter(
    private val onOrderClick: (OrderModel) -> Unit
) : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(order: OrderModel) {
            binding.apply {
                tvOrderId.text = "Order #${order.id.take(8)}"
                tvOrderDate.text = formatDate(order.date?.toDate()?.time ?: 0)
                tvOrderStatus.text = order.status
                tvItemCount.text = "${order.items.values.sum()} items"
                
                // Status color
                val statusColor = when (order.status) {
                    "Ordered" -> android.R.color.holo_blue_dark
                    "Processing" -> android.R.color.holo_orange_dark
                    "Shipped" -> android.R.color.holo_purple
                    "Delivered" -> android.R.color.holo_green_dark
                    "Cancelled" -> android.R.color.holo_red_dark
                    else -> android.R.color.darker_gray
                }
                tvOrderStatus.setTextColor(root.context.getColor(statusColor))
                
                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
        
        private fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            return sdf.format(timestamp)
        }
    }
    
    private class OrderDiffCallback : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }
    }
}
