package com.komputerkit.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.wavesoffood.R
import com.komputerkit.wavesoffood.databinding.ItemOrderDetailBinding
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.viewmodel.OrderDetailItem
import java.text.NumberFormat
import java.util.Locale

class OrderDetailAdapter : ListAdapter<OrderDetailItem, OrderDetailAdapter.OrderDetailViewHolder>(OrderDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val binding = ItemOrderDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderDetailViewHolder(
        private val binding: ItemOrderDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderDetailItem) {
            binding.tvProductName.text = item.product.title
            binding.tvProductPrice.text = formatPrice(item.product.price)
            binding.tvQuantity.text = "Qty: ${item.quantity}"
            binding.tvSubtotal.text = formatPrice(item.product.price * item.quantity)

            // Load image with Glide
            Glide.with(binding.root.context)
                .load(item.product.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(binding.ivProduct)
        }

        private fun formatPrice(price: Double): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(price)
        }
    }

    class OrderDetailDiffCallback : DiffUtil.ItemCallback<OrderDetailItem>() {
        override fun areItemsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: OrderDetailItem, newItem: OrderDetailItem): Boolean {
            return oldItem == newItem
        }
    }
}
