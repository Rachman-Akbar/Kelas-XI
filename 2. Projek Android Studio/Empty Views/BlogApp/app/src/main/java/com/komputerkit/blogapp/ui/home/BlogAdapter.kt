package com.komputerkit.blogapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.komputerkit.blogapp.databinding.ItemBlogListBinding
import com.komputerkit.blogapp.model.Blog
import java.text.SimpleDateFormat
import java.util.*

class BlogAdapter(
    private val onBlogClick: (Blog) -> Unit
) : ListAdapter<Blog, BlogAdapter.BlogViewHolder>(BlogDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlogViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BlogViewHolder(
        private val binding: ItemBlogListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(blog: Blog) {
            binding.tvTitle.text = blog.title
            binding.tvContent.text = blog.content
            binding.tvAuthor.text = "Oleh: ${blog.authorName.ifEmpty { "Anonim" }}"
            binding.tvTimestamp.text = formatTimestamp(blog.timestamp)
            
            binding.root.setOnClickListener {
                onBlogClick(blog)
            }
        }
        
        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            return format.format(date)
        }
    }
    
    class BlogDiffCallback : DiffUtil.ItemCallback<Blog>() {
        override fun areItemsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Blog, newItem: Blog): Boolean {
            return oldItem == newItem
        }
    }
}