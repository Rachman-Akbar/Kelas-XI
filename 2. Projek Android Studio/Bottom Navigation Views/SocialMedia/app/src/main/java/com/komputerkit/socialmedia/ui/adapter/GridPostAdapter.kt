package com.komputerkit.socialmedia.ui.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.databinding.ListItemGridPostBinding

class GridPostAdapter(
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        val binding = ListItemGridPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GridPostViewHolder(
        private val binding: ListItemGridPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                Log.d("GridPostAdapter", "Binding post ${post.postId}, imageBase64 length: ${post.imageBase64.length}, imageUrl: ${post.imageUrl}")
                
                // Load post image with Base64 and URL support
                loadPostImage(post)
                
                // Handle click
                root.setOnClickListener {
                    onPostClick(post)
                }
                
                // Hide indicators for now (can be enhanced later for video/multiple posts)
                ivVideoIndicator.visibility = android.view.View.GONE
                ivMultipleIndicator.visibility = android.view.View.GONE
            }
        }

        private fun loadPostImage(post: Post) {
            binding.apply {
                try {
                    when {
                        post.imageBase64.isNotEmpty() -> {
                            // Load Base64 image
                            Log.d("GridPostAdapter", "Loading Base64 image for post ${post.postId}")
                            val imageBytes = Base64.decode(post.imageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                Glide.with(ivGridPost.context)
                                    .load(bitmap)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .centerCrop()
                                    .into(ivGridPost)
                            } else {
                                loadPlaceholder()
                            }
                        }
                        post.imageUrl.isNotEmpty() -> {
                            // Load URL image
                            Log.d("GridPostAdapter", "Loading URL image for post ${post.postId}: ${post.imageUrl}")
                            Glide.with(ivGridPost.context)
                                .load(post.imageUrl)
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_placeholder)
                                .centerCrop()
                                .into(ivGridPost)
                        }
                        else -> {
                            loadPlaceholder()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GridPostAdapter", "Error loading image for post ${post.postId}: ${e.message}")
                    loadPlaceholder()
                }
            }
        }

        private fun loadPlaceholder() {
            binding.ivGridPost.setImageResource(R.drawable.ic_image_placeholder)
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.postId == newItem.postId
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}