package com.komputerkit.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.komputerkit.whatsapp.activities.ChatDetailActivity
import com.komputerkit.whatsapp.adapters.ChatsAdapter
import com.komputerkit.whatsapp.databinding.FragmentChatsBinding
import com.komputerkit.whatsapp.models.Chat
import com.komputerkit.whatsapp.models.User

class ChatsFragment : Fragment() {
    
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var chatsAdapter: ChatsAdapter
    private val chatsList = mutableListOf<Chat>()
    private val usersList = mutableListOf<User>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        setupRecyclerView()
        loadChats()
    }
    
    private fun setupRecyclerView() {
        chatsAdapter = ChatsAdapter(chatsList, usersList) { chat ->
            val intent = Intent(requireContext(), ChatDetailActivity::class.java)
            val otherUserId = chat.participants.find { it != auth.currentUser?.uid }
            intent.putExtra("userId", otherUserId)
            intent.putExtra("chatId", chat.chatId)
            startActivity(intent)
        }
        
        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
        }
    }
    
    private fun loadChats() {
        auth.currentUser?.let { currentUser ->
            binding.progressBar.visibility = View.VISIBLE
            
            database.reference.child("chats")
                .orderByChild("participants")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatsList.clear()
                        
                        for (chatSnapshot in snapshot.children) {
                            val chat = chatSnapshot.getValue(Chat::class.java)
                            chat?.let {
                                if (it.participants.contains(currentUser.uid)) {
                                    chatsList.add(it)
                                }
                            }
                        }
                        
                        // Sort chats by last message time
                        chatsList.sortByDescending { it.lastMessageTime }
                        
                        loadUsersForChats()
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        binding.progressBar.visibility = View.GONE
                    }
                })
        }
    }
    
    private fun loadUsersForChats() {
        database.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    usersList.clear()
                    
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { usersList.add(it) }
                    }
                    
                    binding.progressBar.visibility = View.GONE
                    
                    if (chatsList.isEmpty()) {
                        binding.tvNoChats.visibility = View.VISIBLE
                        binding.recyclerViewChats.visibility = View.GONE
                    } else {
                        binding.tvNoChats.visibility = View.GONE
                        binding.recyclerViewChats.visibility = View.VISIBLE
                        chatsAdapter.notifyDataSetChanged()
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                }
            })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}