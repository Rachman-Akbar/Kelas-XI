package com.komputerkit.earningapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.komputerkit.earningapp.data.model.Subject
import com.komputerkit.earningapp.databinding.ActivityDifficultySelectionBinding
import com.komputerkit.earningapp.utils.CoinManager
import kotlinx.coroutines.launch

class DifficultySelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDifficultySelectionBinding
    private lateinit var subject: Subject
    private lateinit var coinManager: CoinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDifficultySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize CoinManager
        coinManager = CoinManager.getInstance(this)

        // Get subject data from intent
        subject = intent.getParcelableExtra("subject") ?: run {
            Log.e("DifficultySelection", "No subject data received")
            finish()
            return
        }

        setupUI()
        setupClickListeners()
        loadCoins()
    }

    private fun setupUI() {
        binding.apply {
            tvSubjectName.text = subject.name
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            
            btnEasy.setOnClickListener {
                startQuiz("easy")
            }
            
            btnMedium.setOnClickListener {
                startQuiz("medium")
            }
            
            btnHard.setOnClickListener {
                startQuiz("hard")
            }
        }
    }

    private fun loadCoins() {
        lifecycleScope.launch {
            try {
                val coins = coinManager.loadCoins()
                binding.tvCoins.text = coins.toString()
            } catch (e: Exception) {
                Log.e("DifficultySelection", "Error loading coins", e)
                binding.tvCoins.text = "0"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh coins when returning to this activity
        loadCoins()
    }

    private fun startQuiz(difficulty: String) {
        Log.d("DifficultySelection", "Starting quiz with difficulty: $difficulty")
        
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("subject", subject)
            putExtra("difficulty", difficulty)
        }
        startActivity(intent)
        finish()
    }
}
