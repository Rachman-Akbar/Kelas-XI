package com.komputerkit.socialmedia.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.databinding.ListItemPostBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val onLikeClick: (Post) -> Unit,
    private val onCommentClick: (Post) -> Unit,
    private val onShareClick: (Post) -> Unit,
    private val onBookmarkClick: (Post) -> Unit,
    private val onProfileClick: (Post) -> Unit,
    private val onMenuClick: (Post) -> Unit,
    private val currentUserId: String
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ListItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ListItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                // Header
                tvPostUsername.text = post.username
                
                // Load profile image
                Glide.with(ivPostProfile.context)
                    .load(post.profileImageUrl.ifEmpty { R.mipmap.ic_launcher })
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(ivPostProfile)

                // Load post image - handle Base64 and URLs
                android.util.Log.d("PostAdapter", "Loading image for post ${post.postId}, imageBase64 length: ${post.imageBase64.length}, imageUrl: ${post.imageUrl}")
                
                try {
                    when {
                        post.imageBase64.isNotEmpty() -> {
                            // Load Base64 image
                            android.util.Log.d("PostAdapter", "Loading Base64 image for post ${post.postId}")
                            val imageBytes = android.util.Base64.decode(post.imageBase64, android.util.Base64.DEFAULT)
                            val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                Glide.with(ivPostImage.context)
                                    .load(bitmap)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .centerCrop()
                                    .into(ivPostImage)
                            } else {
                                android.util.Log.e("PostAdapter", "Failed to decode Base64 image for post ${post.postId}")
                                ivPostImage.setImageResource(R.drawable.ic_image_placeholder)
                            }
                        }
                        post.imageUrl.isNotEmpty() -> {
                            // Try loading as Base64 first (in case imageUrl contains Base64)
                            try {
                                val imageBytes = android.util.Base64.decode(post.imageUrl, android.util.Base64.DEFAULT)
                                val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                if (bitmap != null) {
                                    android.util.Log.d("PostAdapter", "Loading Base64 from imageUrl for post ${post.postId}")
                                    Glide.with(ivPostImage.context)
                                        .load(bitmap)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .centerCrop()
                                        .into(ivPostImage)
                                } else {
                                    // Load as URL if Base64 decode fails
                                    android.util.Log.d("PostAdapter", "Loading URL image for post ${post.postId}")
                                    Glide.with(ivPostImage.context)
                                        .load(post.imageUrl)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .centerCrop()
                                        .into(ivPostImage)
                                }
                            } catch (e: Exception) {
                                // Load as URL if Base64 decode fails
                                android.util.Log.d("PostAdapter", "Loading URL image for post ${post.postId} (Base64 failed)")
                                Glide.with(ivPostImage.context)
                                    .load(post.imageUrl)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .centerCrop()
                                    .into(ivPostImage)
                            }
                        }
                        else -> {
                            // Placeholder for no image
                            android.util.Log.d("PostAdapter", "No image for post ${post.postId}")
                            ivPostImage.setImageResource(R.drawable.ic_image_placeholder)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PostAdapter", "Error loading image for post ${post.postId}", e)
                    ivPostImage.setImageResource(R.drawable.ic_image_placeholder)
                }

                // Footer
                val likesCount = post.likes.size
                val likesText = when (likesCount) {
                    0 -> "0 likes"
                    1 -> "1 like"
                    else -> "$likesCount likes"
                }
                tvPostLikes.text = likesText
                
                tvPostCaption.text = if (post.caption.isNotEmpty()) {
                    "${post.username} ${post.caption}"
                } else {
                    post.username
                }

                // Update like button state
                val isLiked = post.likes.contains(currentUserId) || post.isLiked
                ivPostLike.setImageResource(
                    if (isLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                )
                
                // Set click listeners with proper null checks and immediate feedback
                ivPostProfile.setOnClickListener { 
                    it.isEnabled = false
                    onProfileClick(post)
                    it.postDelayed({ it.isEnabled = true }, 1000)
                }
                
                tvPostUsername.setOnClickListener { 
                    it.isEnabled = false
                    onProfileClick(post)
                    it.postDelayed({ it.isEnabled = true }, 1000)
                }
                
                ivPostMenu.setOnClickListener { 
                    it.isEnabled = false
                    onMenuClick(post)
                    it.postDelayed({ it.isEnabled = true }, 1000)
                }
                
                ivPostLike.setOnClickListener { view ->
                    view.isEnabled = false
                    // Immediate visual feedback
                    val isCurrentlyLiked = post.likes.contains(currentUserId) || post.isLiked
                    ivPostLike.setImageResource(
                        if (!isCurrentlyLiked) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                    )
                    onLikeClick(post)
                    view.postDelayed({ view.isEnabled = true }, 2000)
                }
                
                ivPostComment.setOnClickListener { view ->
                    view.isEnabled = false
                    onCommentClick(post)
                    view.postDelayed({ view.isEnabled = true }, 1000)
                }
                
                ivPostShare.setOnClickListener { view ->
                    view.isEnabled = false
                    onShareClick(post)
                    view.postDelayed({ view.isEnabled = true }, 1000)
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