package com.komputerkit.whatsapp.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.komputerkit.whatsapp.R
import com.komputerkit.whatsapp.adapters.MessagesAdapter
import com.komputerkit.whatsapp.databinding.ActivityChatDetailBinding
import com.komputerkit.whatsapp.models.Chat
import com.komputerkit.whatsapp.models.Message
import com.komputerkit.whatsapp.models.User
import com.komputerkit.whatsapp.utils.CircularTransformation

class ChatDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()
    
    private lateinit var receiverUserId: String
    private lateinit var senderUserId: String
    private var chatId: String = ""
    private var typingHandler = Handler(Looper.getMainLooper())
    private var typingRunnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        try {
            auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            
            receiverUserId = intent.getStringExtra("userId") ?: ""
            chatId = intent.getStringExtra("chatId") ?: ""
            senderUserId = auth.currentUser?.uid ?: ""
            
            if (senderUserId.isEmpty()) {
                finish()
                return
            }
            
            if (receiverUserId.isEmpty()) {
                finish()
                return
            }
            
            if (chatId.isEmpty()) {
                chatId = generateChatId(senderUserId, receiverUserId)
            }
            
            setupToolbar()
            setupRecyclerView()
            loadReceiverInfo()
            loadMessages()
            setupSendButton()
            listenForTyping()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        messagesAdapter = MessagesAdapter(messagesList)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        
        binding.recyclerViewMessages.apply {
            this.layoutManager = layoutManager
            adapter = messagesAdapter
        }
    }
    
    private fun loadReceiverInfo() {
        try {
            database.reference.child("users").child(receiverUserId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val user = snapshot.getValue(User::class.java)
                            user?.let {
                                binding.tvUsername.text = it.username ?: "Unknown User"
                                binding.tvStatus.text = if (it.status == "online") "Online" else "Last seen recently"
                                
                                if (!it.profilePic.isNullOrEmpty()) {
                                    Glide.with(this@ChatDetailActivity)
                                        .load(it.profilePic)
                                        .transform(CircularTransformation())
                                        .placeholder(R.drawable.ic_person)
                                        .error(R.drawable.ic_person)
                                        .into(binding.ivProfilePic)
                                } else {
                                    binding.ivProfilePic.setImageResource(R.drawable.ic_person)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadMessages() {
        database.reference.child("chats").child(chatId).child("messages")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messagesList.clear()
                    
                    android.util.Log.d("ChatDetail", "Loading messages for chat: $chatId")
                    android.util.Log.d("ChatDetail", "Messages count: ${snapshot.childrenCount}")
                    
                    for (messageSnapshot in snapshot.children) {
                        try {
                            val message = messageSnapshot.getValue(Message::class.java)
                            message?.let { 
                                messagesList.add(it)
                                android.util.Log.d("ChatDetail", "Loaded message: ${it.messageText}")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("ChatDetail", "Error loading message", e)
                            e.printStackTrace()
                        }
                    }
                    
                    // Sort messages by timestamp to ensure proper order
                    messagesList.sortBy { it.timestamp }
                    
                    messagesAdapter.notifyDataSetChanged()
                    if (messagesList.isNotEmpty()) {
                        binding.recyclerViewMessages.scrollToPosition(messagesList.size - 1)
                    }
                    
                    android.util.Log.d("ChatDetail", "Final messages count: ${messagesList.size}")
                }
                
                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("ChatDetail", "Error loading messages: ${error.message}")
                    error.toException().printStackTrace()
                }
            })
    }
    
    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        // Setup typing indicator
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    setTypingStatus(true)
                } else {
                    setTypingStatus(false)
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setTypingStatus(isTyping: Boolean) {
        try {
            typingRunnable?.let { typingHandler.removeCallbacks(it) }
            
            if (isTyping) {
                database.reference.child("chats").child(chatId)
                    .child("typing").child(senderUserId).setValue(true)
                
                // Auto remove typing status after 3 seconds
                typingRunnable = Runnable {
                    database.reference.child("chats").child(chatId)
                        .child("typing").child(senderUserId).removeValue()
                }
                typingHandler.postDelayed(typingRunnable!!, 3000)
            } else {
                database.reference.child("chats").child(chatId)
                    .child("typing").child(senderUserId).removeValue()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun listenForTyping() {
        database.reference.child("chats").child(chatId).child("typing").child(receiverUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isTyping = snapshot.getValue(Boolean::class.java) ?: false
                    binding.tvTyping.visibility = if (isTyping) View.VISIBLE else View.GONE
                    binding.tvStatus.visibility = if (isTyping) View.GONE else View.VISIBLE
                }
                
                override fun onCancelled(error: DatabaseError) {
                    error.toException().printStackTrace()
                }
            })
    }
    
    private fun sendMessage() {
        val messageText = binding.etMessage.text.toString().trim()
        if (messageText.isEmpty()) return
        
        try {
            // Disable send button temporarily
            binding.btnSend.isEnabled = false
            
            val messageId = database.reference.push().key ?: return
            val timestamp = System.currentTimeMillis()
            
            val message = Message(
                messageId = messageId,
                senderId = senderUserId,
                receiverId = receiverUserId,
                messageText = messageText,
                timestamp = timestamp,
                status = "sending"
            )
            
            // Add message to local list immediately for better UX
            messagesList.add(message)
            messagesAdapter.notifyItemInserted(messagesList.size - 1)
            binding.recyclerViewMessages.scrollToPosition(messagesList.size - 1)
            
            // Clear input immediately for better UX
            binding.etMessage.text.clear()
            
            // Save message to database
            database.reference.child("chats").child(chatId).child("messages").child(messageId)
                .setValue(message)
                .addOnSuccessListener {
                    // Update message status to sent
                    database.reference.child("chats").child(chatId).child("messages").child(messageId)
                        .child("status").setValue("sent")
                    
                    // Update chat info
                    updateChatInfo(messageText, timestamp)
                    // Re-enable send button
                    binding.btnSend.isEnabled = true
                    
                    // Simulate delivered status after 1 second
                    Handler(Looper.getMainLooper()).postDelayed({
                        database.reference.child("chats").child(chatId).child("messages").child(messageId)
                            .child("status").setValue("delivered")
                    }, 1000)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    // Remove the message from local list if failed
                    messagesList.removeLastOrNull()
                    messagesAdapter.notifyItemRemoved(messagesList.size)
                    // Re-enable send button on failure
                    binding.btnSend.isEnabled = true
                    // Restore message text on failure
                    binding.etMessage.setText(messageText)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.btnSend.isEnabled = true
        }
    }
    
    private fun updateChatInfo(lastMessage: String, timestamp: Long) {
        val chat = Chat(
            chatId = chatId,
            participants = listOf(senderUserId, receiverUserId),
            lastMessage = lastMessage,
            lastMessageTime = timestamp,
            lastMessageSender = senderUserId
        )
        
        database.reference.child("chats").child(chatId).setValue(chat)
    }
    
    private fun generateChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up typing status when leaving chat
        try {
            database.reference.child("chats").child(chatId)
                .child("typing").child(senderUserId).removeValue()
            typingRunnable?.let { typingHandler.removeCallbacks(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}