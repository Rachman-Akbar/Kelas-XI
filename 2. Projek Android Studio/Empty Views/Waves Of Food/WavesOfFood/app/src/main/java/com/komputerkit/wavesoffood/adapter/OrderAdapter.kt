package com.komputerkit.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.komputerkit.wavesoffood.databinding.ItemOrderBinding
import com.komputerkit.wavesoffood.data.model.OrderModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val onItemClick: (OrderModel) -> Unit
) : ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onItemClick: (OrderModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderModel) {
            // Display shortened order ID
            val shortOrderId = if (order.id.length > 12) {
                order.id.substring(0, 12) + "..."
            } else {
                order.id
            }
            binding.tvOrderId.text = shortOrderId
            
            // Format date
            val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val dateString = order.date?.toDate()?.let { dateFormat.format(it) } ?: "N/A"
            binding.tvDate.text = dateString
            
            // Item count
            val itemCount = order.items.values.sum()
            binding.tvItemCount.text = "$itemCount items"
            
            // Status
            binding.chipStatus.text = order.status
            
            // You would need to calculate total from items
            // For now, show placeholder
            binding.tvTotal.text = "View Details"
            
            binding.root.setOnClickListener {
                onItemClick(order)
            }
        }

        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem == newItem
        }
    }
}
