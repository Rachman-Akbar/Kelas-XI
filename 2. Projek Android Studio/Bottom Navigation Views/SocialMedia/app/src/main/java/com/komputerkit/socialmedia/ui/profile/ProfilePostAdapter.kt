package com.komputerkit.socialmedia.ui.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.databinding.ListItemGridPostBinding

class ProfilePostAdapter(
    private val context: Context,
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, ProfilePostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ListItemGridPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    inner class PostViewHolder(
        private val binding: ListItemGridPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            Log.d("ProfilePostAdapter", "Binding post ${post.postId}, imageBase64 length: ${post.imageBase64?.length ?: 0}")
            
            // Load post image
            loadPostImage(post)

            // Set click listener
            binding.root.setOnClickListener {
                Log.d("ProfilePostAdapter", "Post clicked: ${post.postId}")
                onPostClick(post)
            }
        }

        private fun loadPostImage(post: Post) {
            try {
                when {
                    !post.imageBase64.isNullOrEmpty() -> {
                        // Handle Base64 image
                        Log.d("ProfilePostAdapter", "Loading Base64 image for post ${post.postId}")
                        val imageBytes = Base64.decode(post.imageBase64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        
                        if (bitmap != null) {
                            binding.ivGridPost.setImageBitmap(bitmap)
                            Log.d("ProfilePostAdapter", "Base64 image loaded successfully for post ${post.postId}")
                        } else {
                            Log.e("ProfilePostAdapter", "Failed to decode Base64 image for post ${post.postId}")
                            // Set placeholder or default image
                            binding.ivGridPost.setImageResource(android.R.drawable.ic_menu_gallery)
                        }
                    }
                    !post.imageUrl.isNullOrEmpty() -> {
                        // Handle URL image
                        Log.d("ProfilePostAdapter", "Loading URL image for post ${post.postId}")
                        Glide.with(context)
                            .load(post.imageUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(binding.ivGridPost)
                    }
                    else -> {
                        // No image available
                        Log.w("ProfilePostAdapter", "No image available for post ${post.postId}")
                        binding.ivGridPost.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfilePostAdapter", "Error loading image for post ${post.postId}: ${e.message}")
                binding.ivGridPost.setImageResource(android.R.drawable.ic_menu_gallery)
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
}