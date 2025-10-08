package com.komputerkit.earningapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.earningapp.data.model.LeaderboardEntry
import com.komputerkit.earningapp.ui.adapter.LeaderboardAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardActivity : BaseActivity() {

    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Hide action bar
        supportActionBar?.hide()

        initViews()
        setupFirebase()
        setupRecyclerView()
        setupHeader()
        setupFooter()
        loadLeaderboard()
    }

    private fun initViews() {
        try {
            rvLeaderboard = findViewById(R.id.rvLeaderboard)
            progressBar = findViewById(R.id.progressBar)
            tvEmptyState = findViewById(R.id.tvEmptyState)
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Error initializing views", e)
        }
    }

    private fun setupFirebase() {
        try {
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Error setting up Firebase", e)
        }
    }

    private fun setupRecyclerView() {
        try {
            leaderboardAdapter = LeaderboardAdapter()
            rvLeaderboard.apply {
                layoutManager = LinearLayoutManager(this@LeaderboardActivity)
                adapter = leaderboardAdapter
            }
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Error setting up RecyclerView", e)
        }
    }

    private fun updateUserInfo() {
        try {
            val currentUser = firebaseAuth.currentUser
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            
            val userName = currentUser?.displayName
                ?: sharedPreferences.getString("user_name", null)
                ?: currentUser?.email?.substringBefore("@")
                ?: "User"
            
            tvUserName?.text = userName
            
            // Update coin balance from SharedPreferences
            val coinBalance = sharedPreferences.getInt("coin_balance", 100)
            tvCoinBalanceHeader?.text = coinBalance.toString()
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Error updating user info", e)
        }
    }

    private fun loadLeaderboard() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                
                val leaderboardData = firestore.collection("leaderboard")
                    .orderBy("totalScore", Query.Direction.DESCENDING)
                    .limit(100)
                    .get()
                    .await()

                val leaderboardList = mutableListOf<LeaderboardEntry>()
                
                leaderboardData.documents.forEachIndexed { index, document ->
                    try {
                        val entry = document.toObject(LeaderboardEntry::class.java)
                        entry?.let {
                            it.rank = index + 1
                            leaderboardList.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e("LeaderboardActivity", "Error parsing leaderboard entry", e)
                    }
                }

                showLoading(false)
                
                if (leaderboardList.isNotEmpty()) {
                    rvLeaderboard.visibility = View.VISIBLE
                    tvEmptyState.visibility = View.GONE
                    leaderboardAdapter.updateLeaderboard(leaderboardList)
                    Log.d("LeaderboardActivity", "Loaded ${leaderboardList.size} leaderboard entries")
                } else {
                    rvLeaderboard.visibility = View.GONE
                    tvEmptyState.visibility = View.VISIBLE
                    Log.w("LeaderboardActivity", "No leaderboard data found")
                }

            } catch (e: Exception) {
                Log.e("LeaderboardActivity", "Error loading leaderboard", e)
                showLoading(false)
                rvLeaderboard.visibility = View.GONE
                tvEmptyState.visibility = View.VISIBLE
                Toast.makeText(this@LeaderboardActivity, "Gagal memuat leaderboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        try {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            rvLeaderboard.visibility = if (show) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Error updating loading state", e)
        }
    }
}
