package com.komputerkit.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.whatsapp.R
import com.komputerkit.whatsapp.databinding.ItemMessageReceivedBinding
import com.komputerkit.whatsapp.databinding.ItemMessageSentBinding
import com.komputerkit.whatsapp.models.Message
import java.text.SimpleDateFormat
import java.util.*

class MessagesAdapter(
    private val messagesList: MutableList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
    
    private val auth = FirebaseAuth.getInstance()
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]
        return if (message.senderId == auth.currentUser?.uid) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedMessageViewHolder(binding)
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messagesList[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }
    
    override fun getItemCount(): Int = messagesList.size
    
    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.messageText.ifEmpty { "Message" }
            binding.tvTime.text = try {
                timeFormat.format(Date(message.timestamp))
            } catch (e: Exception) {
                timeFormat.format(Date())
            }
            
            // Set message status icon
            when (message.status.lowercase()) {
                "sending" -> {
                    binding.ivMessageStatus.setImageResource(R.drawable.ic_clock)
                    binding.ivMessageStatus.clearColorFilter()
                }
                "sent" -> {
                    binding.ivMessageStatus.setImageResource(R.drawable.ic_check)
                    binding.ivMessageStatus.clearColorFilter()
                }
                "delivered" -> {
                    binding.ivMessageStatus.setImageResource(R.drawable.ic_double_check)
                    binding.ivMessageStatus.clearColorFilter()
                }
                "read" -> {
                    binding.ivMessageStatus.setImageResource(R.drawable.ic_double_check)
                    binding.ivMessageStatus.setColorFilter(
                        ContextCompat.getColor(itemView.context, R.color.whatsapp_accent)
                    )
                }
                else -> {
                    binding.ivMessageStatus.setImageResource(R.drawable.ic_clock)
                    binding.ivMessageStatus.clearColorFilter()
                }
            }
        }
    }
    
    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.tvMessage.text = message.messageText.ifEmpty { "Message" }
            binding.tvTime.text = try {
                timeFormat.format(Date(message.timestamp))
            } catch (e: Exception) {
                timeFormat.format(Date())
            }
        }
    }
}