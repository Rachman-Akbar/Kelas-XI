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
import com.komputerkit.socialmedia.data.model.Story
import com.komputerkit.socialmedia.databinding.ItemStoryBinding

class StoryAdapter(
    private val onStoryClick: (Story) -> Unit
) : ListAdapter<Story, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StoryViewHolder(
        private val binding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.apply {
                tvStoryUsername.text = story.username

                // Load profile image with Base64 support
                loadProfileImage(story)

                root.setOnClickListener {
                    onStoryClick(story)
                }
            }
        }

        private fun loadProfileImage(story: Story) {
            binding.apply {
                try {
                    when {
                        story.userProfileImageBase64.isNotEmpty() -> {
                            // Load from Base64
                            val imageBytes = Base64.decode(story.userProfileImageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                Glide.with(ivStoryProfile.context)
                                    .load(bitmap)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .circleCrop()
                                    .into(ivStoryProfile)
                            } else {
                                loadDefaultProfileImage()
                            }
                        }
                        story.profileImageUrl.isNotEmpty() -> {
                            // Load from URL
                            Glide.with(ivStoryProfile.context)
                                .load(story.profileImageUrl)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .circleCrop()
                                .into(ivStoryProfile)
                        }
                        story.userProfileImageUrl.isNotEmpty() -> {
                            // Load from alternative URL field
                            Glide.with(ivStoryProfile.context)
                                .load(story.userProfileImageUrl)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .circleCrop()
                                .into(ivStoryProfile)
                        }
                        else -> {
                            loadDefaultProfileImage()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StoryAdapter", "Error loading profile image for story ${story.storyId}: ${e.message}")
                    loadDefaultProfileImage()
                }
            }
        }

        private fun loadDefaultProfileImage() {
            binding.apply {
                Glide.with(ivStoryProfile.context)
                    .load(R.mipmap.ic_launcher)
                    .circleCrop()
                    .into(ivStoryProfile)
            }
        }
    }

    private class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.storyId == newItem.storyId
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }
}