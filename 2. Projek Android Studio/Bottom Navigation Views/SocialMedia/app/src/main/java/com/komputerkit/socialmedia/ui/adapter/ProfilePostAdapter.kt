package com.komputerkit.socialmedia.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.databinding.ListItemGridPostBinding

class ProfilePostAdapter(
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, ProfilePostAdapter.ProfilePostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostViewHolder {
        val binding = ListItemGridPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfilePostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfilePostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProfilePostViewHolder(
        private val binding: ListItemGridPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                android.util.Log.d("ProfilePostAdapter", "Binding post ${post.postId}, imageBase64 length: ${post.imageBase64.length}, imageUrl: ${post.imageUrl}")
                
                // Handle Base64 images properly
                try {
                    when {
                        post.imageBase64.isNotEmpty() -> {
                            // Load Base64 image
                            android.util.Log.d("ProfilePostAdapter", "Loading Base64 image for post ${post.postId}")
                            val imageBytes = android.util.Base64.decode(post.imageBase64, android.util.Base64.DEFAULT)
                            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                Glide.with(ivGridPost.context)
                                    .load(bitmap)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .centerCrop()
                                    .into(ivGridPost)
                            } else {
                                android.util.Log.e("ProfilePostAdapter", "Failed to decode Base64 image for post ${post.postId}")
                                ivGridPost.setImageResource(R.drawable.ic_image_placeholder)
                            }
                        }
                        post.imageUrl.isNotEmpty() -> {
                            // Try loading as Base64 first (in case imageUrl contains Base64)
                            try {
                                val imageBytes = android.util.Base64.decode(post.imageUrl, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                if (bitmap != null) {
                                    android.util.Log.d("ProfilePostAdapter", "Loading Base64 from imageUrl for post ${post.postId}")
                                    Glide.with(ivGridPost.context)
                                        .load(bitmap)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .centerCrop()
                                        .into(ivGridPost)
                                } else {
                                    // Load as URL if Base64 decode fails
                                    android.util.Log.d("ProfilePostAdapter", "Loading URL image for post ${post.postId}")
                                    Glide.with(ivGridPost.context)
                                        .load(post.imageUrl)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .centerCrop()
                                        .into(ivGridPost)
                                }
                            } catch (e: Exception) {
                                // Load as URL if Base64 decode fails
                                android.util.Log.d("ProfilePostAdapter", "Loading URL image for post ${post.postId} (Base64 failed)")
                                Glide.with(ivGridPost.context)
                                    .load(post.imageUrl)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .centerCrop()
                                    .into(ivGridPost)
                            }
                        }
                        else -> {
                            // Placeholder for no image
                            android.util.Log.d("ProfilePostAdapter", "No image for post ${post.postId}")
                            ivGridPost.setImageResource(R.drawable.ic_image_placeholder)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProfilePostAdapter", "Error loading image for post ${post.postId}", e)
                    ivGridPost.setImageResource(R.drawable.ic_image_placeholder)
                }

                root.setOnClickListener {
                    onPostClick(post)
                }
            }
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}