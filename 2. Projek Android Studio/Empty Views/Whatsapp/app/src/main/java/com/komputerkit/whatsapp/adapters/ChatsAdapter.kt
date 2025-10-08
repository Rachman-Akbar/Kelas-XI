package com.komputerkit.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.whatsapp.R
import com.komputerkit.whatsapp.databinding.ItemChatBinding
import com.komputerkit.whatsapp.models.Chat
import com.komputerkit.whatsapp.models.User
import com.komputerkit.whatsapp.utils.CircularTransformation
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class ChatsAdapter(
    private val chatsList: List<Chat>,
    private val usersList: List<User>,
    private val onChatClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {
    
    private val auth = FirebaseAuth.getInstance()
    private val prettyTime = PrettyTime()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatsList[position])
    }
    
    override fun getItemCount(): Int = chatsList.size
    
    inner class ChatViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(chat: Chat) {
            val currentUserId = auth.currentUser?.uid
            val otherUserId = chat.participants.find { it != currentUserId }
            val otherUser = usersList.find { it.uid == otherUserId }
            
            otherUser?.let { user ->
                binding.tvUsername.text = user.username
                
                if (user.profilePic.isNotEmpty()) {
                    Glide.with(binding.root.context)
                        .load(user.profilePic)
                        .transform(CircularTransformation())
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivProfilePic)
                } else {
                    binding.ivProfilePic.setImageResource(R.drawable.ic_person)
                }
            }
            
            binding.tvLastMessage.text = if (chat.lastMessage.isEmpty()) "No messages yet" else chat.lastMessage
            binding.tvTime.text = prettyTime.format(Date(chat.lastMessageTime))
            
            binding.root.setOnClickListener {
                onChatClick(chat)
            }
        }
    }
}