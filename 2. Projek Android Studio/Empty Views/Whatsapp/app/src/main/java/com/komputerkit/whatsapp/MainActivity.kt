package com.komputerkit.whatsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.komputerkit.whatsapp.activities.SettingsActivity
import com.komputerkit.whatsapp.activities.SignInActivity
import com.komputerkit.whatsapp.activities.UsersActivity
import com.komputerkit.whatsapp.adapters.MainPagerAdapter
import com.komputerkit.whatsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        // Check if user is signed in
        if (auth.currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }
        
        setupToolbar()
        setupViewPager()
        setupFab()
        setupBackPressedHandler()
        updateUserStatus(true)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }
    
    private fun setupViewPager() {
        try {
            val adapter = MainPagerAdapter(this)
            binding.viewPager.adapter = adapter
            
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Chats"
                    1 -> "Status"
                    2 -> "Calls"
                    else -> ""
                }
            }.attach()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun setupFab() {
        binding.fabNewChat.setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java))
        }
    }
    
    private fun updateUserStatus(isOnline: Boolean) {
        try {
            auth.currentUser?.let { user ->
                val status = if (isOnline) "online" else "offline"
                val lastSeen = System.currentTimeMillis()
                
                database.reference.child("users").child(user.uid).child("status").setValue(status)
                database.reference.child("users").child(user.uid).child("lastSeen").setValue(lastSeen)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateUserStatus(true)
    }
    
    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                updateUserStatus(false)
                auth.signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - prevent automatic logout on back press
                // User must use logout menu item to sign out
            }
        })
    }
}