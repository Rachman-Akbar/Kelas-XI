package com.komputerkit.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.komputerkit.whatsapp.R
import com.komputerkit.whatsapp.databinding.ItemUserBinding
import com.komputerkit.whatsapp.models.User
import com.komputerkit.whatsapp.utils.CircularTransformation

class UsersAdapter(
    private val usersList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(usersList[position])
    }
    
    override fun getItemCount(): Int = usersList.size
    
    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(user: User) {
            binding.tvUsername.text = user.username
            binding.tvEmail.text = user.email
            binding.tvStatus.text = user.status
            
            if (user.profilePic.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(user.profilePic)
                    .transform(CircularTransformation())
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfilePic)
            } else {
                binding.ivProfilePic.setImageResource(R.drawable.ic_person)
            }
            
            binding.root.setOnClickListener {
                onUserClick(user)
            }
        }
    }
}