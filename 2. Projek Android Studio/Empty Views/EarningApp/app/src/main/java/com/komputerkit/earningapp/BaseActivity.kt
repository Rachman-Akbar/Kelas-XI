package com.komputerkit.earningapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.earningapp.ui.auth.LoginActivity
import com.komputerkit.earningapp.utils.CoinManager
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var sharedPreferences: SharedPreferences
    protected lateinit var coinManager: CoinManager
    protected var tvUserName: TextView? = null
    protected var tvCoinBalanceHeader: TextView? = null
    protected var ivUserAvatar: ImageView? = null
    
    // Footer navigation buttons
    private var btnHome: LinearLayout? = null
    private var btnLeaderboard: LinearLayout? = null
    private var btnSpinWheel: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase, SharedPreferences, and CoinManager
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        coinManager = CoinManager.getInstance(this)
    }

    protected fun setupHeader() {
        try {
            // Find header views - these should be present in layouts that include layout_header
            tvUserName = findViewById(R.id.tvUserName)
            tvCoinBalanceHeader = findViewById(R.id.tvCoinBalance)
            ivUserAvatar = findViewById(R.id.ivUserAvatar)

            // Check if views were found
            if (tvUserName == null || tvCoinBalanceHeader == null || ivUserAvatar == null) {
                Log.w("BaseActivity", "Some header views not found, header setup skipped")
                return
            }

            // Update header information
            updateHeaderInfo()

            // Set profile picture click listener
            ivUserAvatar?.setOnClickListener {
                try {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("BaseActivity", "Error opening ProfileActivity", e)
                }
            }
        } catch (e: Exception) {
            Log.e("BaseActivity", "Error setting up header", e)
        }
    }

    protected fun setupFooter() {
        try {
            Log.d("BaseActivity", "Setting up footer navigation")
            // Find footer views - these should be present in layouts that include layout_footer
            btnHome = findViewById(R.id.btnHome)
            btnLeaderboard = findViewById(R.id.btnLeaderboard)
            btnSpinWheel = findViewById(R.id.btnSpinWheel)

            // Check if views were found
            if (btnHome == null) {
                Log.e("BaseActivity", "btnHome not found!")
                return
            }
            if (btnLeaderboard == null) {
                Log.e("BaseActivity", "btnLeaderboard not found!")
                return
            }
            if (btnSpinWheel == null) {
                Log.e("BaseActivity", "btnSpinWheel not found!")
                return
            }

            Log.d("BaseActivity", "All footer buttons found successfully")
            setupFooterNavigation()
            highlightCurrentTab()
        } catch (e: Exception) {
            Log.e("BaseActivity", "Error setting up footer", e)
        }
    }

    private fun setupFooterNavigation() {
        btnHome?.setOnClickListener {
            Log.d("BaseActivity", "Home button clicked")
            if (this !is MainActivity) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                Log.d("BaseActivity", "Starting MainActivity")
                startActivity(intent)
            } else {
                Log.d("BaseActivity", "Already in MainActivity")
            }
        }

        btnLeaderboard?.setOnClickListener {
            if (this !is LeaderboardActivity) {
                val intent = Intent(this, LeaderboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

        btnSpinWheel?.setOnClickListener {
            if (this !is SpinWheelActivity) {
                val intent = Intent(this, SpinWheelActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
    }

    private fun highlightCurrentTab() {
        try {
            // Reset all tabs to default state
            btnHome?.let { resetTabHighlight(it) }
            btnLeaderboard?.let { resetTabHighlight(it) }
            btnSpinWheel?.let { resetTabHighlight(it) }

            // Highlight current tab based on activity
            when (this::class.java) {
                MainActivity::class.java -> btnHome?.let { highlightTab(it) } // Default to home tab
                LeaderboardActivity::class.java -> btnLeaderboard?.let { highlightTab(it) }
                SpinWheelActivity::class.java -> btnSpinWheel?.let { highlightTab(it) }
            }
        } catch (e: Exception) {
            Log.e("BaseActivity", "Error highlighting current tab", e)
        }
    }

    private fun highlightTab(tab: LinearLayout) {
        tab.alpha = 1.0f
        val imageView = tab.getChildAt(0) as ImageView
        val textView = tab.getChildAt(1) as TextView
        imageView.setColorFilter(getColor(R.color.primary_color))
        textView.setTextColor(getColor(R.color.primary_color))
    }

    private fun resetTabHighlight(tab: LinearLayout) {
        tab.alpha = 0.6f
        val imageView = tab.getChildAt(0) as ImageView
        val textView = tab.getChildAt(1) as TextView
        imageView.setColorFilter(getColor(R.color.text_secondary))
        textView.setTextColor(getColor(R.color.text_secondary))
    }

    protected fun updateHeaderInfo() {
        try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val userName = user.displayName ?: user.email ?: "User"
                tvUserName?.text = userName

                // Use CoinManager for consistent coin display
                lifecycleScope.launch {
                    try {
                        val coinBalance = coinManager.loadCoins()
                        tvCoinBalanceHeader?.text = coinBalance.toString()
                        Log.d("BaseActivity", "Header coin balance updated via CoinManager: $coinBalance")
                    } catch (e: Exception) {
                        Log.e("BaseActivity", "Error loading coins via CoinManager for header", e)
                        // Fallback to SharedPreferences
                        val fallbackCoins = sharedPreferences.getInt("coins", 100)
                        tvCoinBalanceHeader?.text = fallbackCoins.toString()
                        Log.d("BaseActivity", "Header coin balance updated via fallback: $fallbackCoins")
                    }
                }
            } else {
                tvUserName?.text = "Guest"
                tvCoinBalanceHeader?.text = "100" // Default for guest
            }
        } catch (e: Exception) {
            Log.e("BaseActivity", "Error updating header info", e)
        }
    }

    /**
     * Refresh only the coin balance in header (useful after earning coins)
     */
    protected fun refreshCoinDisplay() {
        lifecycleScope.launch {
            try {
                val coinBalance = coinManager.loadCoins()
                tvCoinBalanceHeader?.text = coinBalance.toString()
                Log.d("BaseActivity", "Coin display refreshed: $coinBalance")
            } catch (e: Exception) {
                Log.e("BaseActivity", "Error refreshing coin display", e)
            }
        }
    }
}
