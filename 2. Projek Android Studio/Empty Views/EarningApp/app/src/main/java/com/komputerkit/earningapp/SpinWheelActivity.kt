package com.komputerkit.earningapp

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.utils.CoinManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class SpinWheelActivity : BaseActivity() {

    private lateinit var wheelImage: ImageView
    private lateinit var btnSpin: Button
    private lateinit var tvResult: TextView
    
    private lateinit var firestore: FirebaseFirestore
    
    private var coinBalance = 100
    private var isSpinning = false
    private var dailySpinsUsed = 0
    private val maxDailySpins = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin_wheel)

        // Hide action bar
        supportActionBar?.hide()

        initViews()
        setupFirebase()
        setupHeader()
        setupFooter()
        setupSpinWheel()
        updateUserInfo()
        checkDailySpins()
    }

    private fun initViews() {
        try {
            wheelImage = findViewById(R.id.wheelImage)
            btnSpin = findViewById(R.id.btnSpin)
            tvResult = findViewById(R.id.tvResult)
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error initializing views", e)
        }
    }

    private fun setupFirebase() {
        try {
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error setting up Firebase", e)
        }
    }

    private fun checkDailySpins() {
        try {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            val lastSpinDate = sharedPreferences.getString("last_spin_date", "")
            
            if (today == lastSpinDate) {
                dailySpinsUsed = sharedPreferences.getInt("daily_spins_used", 0)
            } else {
                // Reset daily spins for new day
                dailySpinsUsed = 0
                sharedPreferences.edit()
                    .putString("last_spin_date", today)
                    .putInt("daily_spins_used", 0)
                    .apply()
            }
            
            updateSpinButton()
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error checking daily spins", e)
        }
    }

    private fun updateSpinButton() {
        try {
            val remaining = maxDailySpins - dailySpinsUsed
            if (remaining > 0) {
                btnSpin.isEnabled = true
                btnSpin.text = if (remaining == maxDailySpins) {
                    "PUTAR RODA SEKARANG!"
                } else {
                    "PUTAR LAGI ($remaining tersisa)"
                }
            } else {
                btnSpin.isEnabled = false
                btnSpin.text = "Habis untuk hari ini"
                tvResult.text = "🕐 Kembali besok untuk mendapatkan 5 spin gratis lagi!"
            }
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error updating spin button", e)
        }
    }

    private fun setupSpinWheel() {
        btnSpin.setOnClickListener {
            if (!isSpinning && dailySpinsUsed < maxDailySpins) {
                spinWheel()
            }
        }
    }

    private fun spinWheel() {
        if (isSpinning) return
        
        isSpinning = true
        btnSpin.isEnabled = false
        tvResult.text = "🎲 Spinning..."
        
        // Random rotation between 1800-2880 degrees (5-8 full rotations)
        val randomRotation = Random.nextFloat() * 1080 + 1800
        
        val animator = ObjectAnimator.ofFloat(wheelImage, "rotation", randomRotation)
        animator.duration = 4000 // Longer animation for better effect
        animator.interpolator = android.view.animation.DecelerateInterpolator(2f)
        
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d("SpinWheelActivity", "Wheel spinning started")
            }
            override fun onAnimationEnd(animation: Animator) {
                isSpinning = false
                Log.d("SpinWheelActivity", "Wheel spinning ended at $randomRotation degrees")
                processPrize(randomRotation)
            }
            override fun onAnimationCancel(animation: Animator) {
                isSpinning = false
                btnSpin.isEnabled = true
                Log.d("SpinWheelActivity", "Wheel spinning cancelled")
            }
            override fun onAnimationRepeat(animation: Animator) {}
        })
        
        animator.start()
    }

    private fun processPrize(rotation: Float) {
        lifecycleScope.launch {
            try {
                // Calculate prize based on final rotation (8 segments of 45 degrees each)
                val normalizedRotation = rotation % 360
                val segment = (normalizedRotation / 45).toInt()
                
                val prize = when (segment) {
                    0, 4 -> 100 // Gold segments (2 chances out of 8)
                    1, 5 -> 50  // Green segments (2 chances out of 8)
                    2, 6 -> 25  // Orange segments (2 chances out of 8)
                    3, 7 -> 10  // Red segments (2 chances out of 8)
                    else -> 10  // Fallback
                }
                
                // Update daily spins
                dailySpinsUsed++
                sharedPreferences.edit()
                    .putInt("daily_spins_used", dailySpinsUsed)
                    .apply()
                
                // Update coin balance using CoinManager for consistency
                lifecycleScope.launch {
                    try {
                        val newCoins = coinManager.addCoins(prize)
                        coinBalance = newCoins
                        
                        // Update UI with improved message
                        val prizeMessage = when (prize) {
                            100 -> "🎉 JACKPOT! Anda mendapat $prize koin! 🎉"
                            50 -> "🌟 Hebat! Anda mendapat $prize koin! 🌟"
                            25 -> "✨ Bagus! Anda mendapat $prize koin! ✨"
                            else -> "🎁 Anda mendapat $prize koin! 🎁"
                        }
                        tvResult.text = prizeMessage
                        refreshCoinDisplay() // Use BaseActivity method for consistent header update
                        updateSpinButton()
                        
                        Log.d("SpinWheelActivity", "Prize awarded: $prize coins, New total: $newCoins")
                        
                        // Save spin result to Firebase
                        saveSpinResultToFirebase(prize)
                        
                    } catch (e: Exception) {
                        Log.e("SpinWheelActivity", "Error updating coins via CoinManager", e)
                        // Fallback to old method if CoinManager fails
                        coinBalance = sharedPreferences.getInt("coins", 100)
                        coinBalance += prize
                        sharedPreferences.edit()
                            .putInt("coins", coinBalance)
                            .apply()
                        
                        tvResult.text = "Selamat! Anda mendapat $prize koin!"
                        refreshCoinDisplay() // Use BaseActivity method for consistent header update
                        updateSpinButton()
                        
                        Log.d("SpinWheelActivity", "Prize awarded via fallback: $prize coins, Total: $coinBalance")
                    }
                }
                
            } catch (e: Exception) {
                Log.e("SpinWheelActivity", "Error processing prize", e)
                btnSpin.isEnabled = true
                updateSpinButton()
            }
        }
    }

    private suspend fun saveSpinResultToFirebase(prize: Int) {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val spinResult = mapOf(
                    "userId" to currentUser.uid,
                    "prize" to prize,
                    "timestamp" to System.currentTimeMillis(),
                    "type" to "daily_spin"
                )
                
                firestore.collection("user_activities")
                    .add(spinResult)
                    .await()
                
                Log.d("SpinWheelActivity", "Spin result saved to Firebase")
            }
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error saving to Firebase", e)
        }
    }

    private fun updateUserInfo() {
        try {
            // Load coin balance using CoinManager for consistency
            lifecycleScope.launch {
                try {
                    coinBalance = coinManager.loadCoins()
                    refreshCoinDisplay() // Use BaseActivity method for consistent header update
                    Log.d("SpinWheelActivity", "Coin balance loaded via CoinManager: $coinBalance")
                } catch (e: Exception) {
                    Log.e("SpinWheelActivity", "Error loading coins via CoinManager", e)
                    // Fallback to SharedPreferences
                    coinBalance = sharedPreferences.getInt("coins", 100)
                    refreshCoinDisplay() // Use BaseActivity method for consistent header update
                    Log.d("SpinWheelActivity", "Coin balance loaded via fallback: $coinBalance")
                }
            }
        } catch (e: Exception) {
            Log.e("SpinWheelActivity", "Error updating user info", e)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
        checkDailySpins()
    }
}
