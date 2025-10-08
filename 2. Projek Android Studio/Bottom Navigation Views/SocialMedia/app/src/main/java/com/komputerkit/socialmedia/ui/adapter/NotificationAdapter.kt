package com.komputerkit.socialmedia.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.model.Notification
import com.komputerkit.socialmedia.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationAdapter(
    private val onNotificationClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                // Load profile image
                Glide.with(ivNotificationProfile.context)
                    .load(notification.fromUserProfileUrl.ifEmpty { R.mipmap.ic_launcher })
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(ivNotificationProfile)

                // Set username and message
                tvNotificationText.text = "${notification.fromUsername} ${notification.message}"
                
                // Set timestamp
                tvNotificationTime.text = getTimeAgo(notification.timestamp)

                // Show/hide post image based on notification type
                if (notification.postImageUrl != null && notification.type != "follow") {
                    ivNotificationPost.visibility = View.VISIBLE
                    Glide.with(ivNotificationPost.context)
                        .load(notification.postImageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .centerCrop()
                        .into(ivNotificationPost)
                } else {
                    ivNotificationPost.visibility = View.GONE
                }

                // Set read/unread indicator
                val alpha = if (notification.isRead) 0.6f else 1.0f
                root.alpha = alpha

                // Set background color based on read status
                root.setBackgroundColor(
                    if (notification.isRead) 
                        root.context.getColor(android.R.color.transparent)
                    else 
                        root.context.getColor(android.R.color.white)
                )

                root.setOnClickListener {
                    onNotificationClick(notification)
                }
            }
        }

        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d"
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}