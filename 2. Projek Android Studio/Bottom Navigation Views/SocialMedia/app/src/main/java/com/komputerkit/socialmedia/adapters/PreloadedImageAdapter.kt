package com.komputerkit.socialmedia.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.socialmedia.R
import com.komputerkit.socialmedia.databinding.ItemPreloadedImageBinding
import com.komputerkit.socialmedia.models.PreloadedImage

class PreloadedImageAdapter(
    private val imageType: String,
    private val onImageClick: (PreloadedImage) -> Unit
) : ListAdapter<PreloadedImage, PreloadedImageAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPreloadedImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPreloadedImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onImageClick(getItem(position))
                }
            }
        }

        fun bind(image: PreloadedImage) {
            binding.apply {
                // Set category
                textCategory.text = image.category.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
                
                // Set title
                textTitle.text = image.title
                
                // Handle different types
                when (image.type) {
                    "background" -> {
                        // For background colors
                        image.color?.let { colorString ->
                            try {
                                val color = Color.parseColor(colorString)
                                imageView.setBackgroundColor(color)
                                imageView.setImageResource(R.drawable.ic_palette)
                                textTitle.text = image.name ?: "Color"
                                textCategory.text = "Background"
                            } catch (e: Exception) {
                                // Fallback to placeholder
                                imageView.setImageResource(R.drawable.ic_placeholder)
                            }
                        }
                    }
                    else -> {
                        // For regular images
                        Glide.with(binding.root.context)
                            .load(image.url)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                            .centerCrop()
                            .into(imageView)
                    }
                }
                
                // Adjust image height based on type
                val layoutParams = imageView.layoutParams
                layoutParams.height = when (imageType) {
                    "story" -> dpToPx(160) // Taller for stories (9:16 aspect ratio)
                    "post" -> dpToPx(120)  // Square for posts (1:1 aspect ratio)
                    else -> dpToPx(120)
                }
                imageView.layoutParams = layoutParams
            }
        }
        
        private fun dpToPx(dp: Int): Int {
            return (dp * binding.root.context.resources.displayMetrics.density).toInt()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PreloadedImage>() {
        override fun areItemsTheSame(oldItem: PreloadedImage, newItem: PreloadedImage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PreloadedImage, newItem: PreloadedImage): Boolean {
            return oldItem == newItem
        }
    }
}