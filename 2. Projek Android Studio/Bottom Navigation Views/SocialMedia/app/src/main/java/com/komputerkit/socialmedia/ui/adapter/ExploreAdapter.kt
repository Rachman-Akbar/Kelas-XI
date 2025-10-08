package com.komputerkit.socialmedia.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.databinding.ItemExplorePostBinding

class ExploreAdapter(
    private val onPostClick: (Post) -> Unit
) : ListAdapter<Post, ExploreAdapter.ExploreViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = ItemExplorePostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExploreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExploreViewHolder(
        private val binding: ItemExplorePostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                // Load post image
                Glide.with(ivExploreImage.context)
                    .load(post.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(ivExploreImage)

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