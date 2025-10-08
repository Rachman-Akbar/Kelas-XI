package com.komputerkit.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.komputerkit.whatsapp.adapters.UsersAdapter
import com.komputerkit.whatsapp.databinding.ActivityUsersBinding
import com.komputerkit.whatsapp.models.User

class UsersActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityUsersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersAdapter: UsersAdapter
    private val usersList = mutableListOf<User>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        setupToolbar()
        setupRecyclerView()
        loadUsers()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter(usersList) { user ->
            val intent = Intent(this, ChatDetailActivity::class.java)
            intent.putExtra("userId", user.uid)
            intent.putExtra("username", user.username)
            startActivity(intent)
            finish()
        }
        
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersActivity)
            adapter = usersAdapter
        }
    }
    
    private fun loadUsers() {
        binding.progressBar.visibility = View.VISIBLE
        
        database.reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    usersList.clear()
                    
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let {
                            // Don't show current user in the list
                            if (it.uid != auth.currentUser?.uid) {
                                usersList.add(it)
                            }
                        }
                    }
                    
                    binding.progressBar.visibility = View.GONE
                    usersAdapter.notifyDataSetChanged()
                }
                
                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                }
            })
    }
}