package com.komputerkit.earningapp

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.komputerkit.earningapp.data.model.Subject
import com.komputerkit.earningapp.data.repository.QuizRepository
import com.komputerkit.earningapp.ui.adapter.SubjectsAdapter
import com.komputerkit.earningapp.utils.Resource
import com.komputerkit.earningapp.utils.CoinManager
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    // Firebase related
    private lateinit var subjectsAdapter: SubjectsAdapter
    private lateinit var quizRepository: QuizRepository
    private lateinit var rvSubjects: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmptyState: LinearLayout
    
    private var coinBalance = 100 // Starting balance

    // Activity result launcher for edit profile
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Refresh user info after profile edit
            updateUserInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("MainActivity", "=== MAIN ACTIVITY STARTING ===")
        Log.d("MainActivity", "onCreate() called")
        
        try {
            Log.d("MainActivity", "Setting content view to activity_main")
            setContentView(R.layout.activity_main)
            Log.d("MainActivity", "Content view set successfully")

            // Hide action bar
            supportActionBar?.hide()
            Log.d("MainActivity", "Action bar hidden")

            // Initialize views safely
            try {
                Log.d("MainActivity", "Initializing views")
                initViews()
                Log.d("MainActivity", "Views initialized successfully")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing views", e)
            }
            
            // Initialize Firebase and SharedPreferences
            try {
                Log.d("MainActivity", "Initializing Firebase and SharedPreferences")
                firebaseAuth = FirebaseAuth.getInstance()
                sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                quizRepository = QuizRepository()
                Log.d("MainActivity", "Firebase, SharedPreferences, and QuizRepository initialized successfully")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing Firebase/SharedPreferences/QuizRepository", e)
            }
            
            // Setup header actions safely
            try {
                setupHeader()
                setupHeaderActions()
                setupFooter()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error setting up header actions", e)
            }
            
            // Setup UI components
            try {
                setupSubjectsRecyclerView()
                observeViewModel()
                loadSubjects()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error setting up subjects UI", e)
            }
            
            // Update user info and coin balance safely
            try {
                updateUserInfo()
                updateCoinBalance()
                
                // Sync coins if user is logged in (in case they earned coins offline)
                lifecycleScope.launch {
                    try {
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser != null) {
                            coinManager.syncCoins()
                            // Refresh the display after sync
                            updateCoinBalance()
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error syncing coins", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error updating user info", e)
            }
            
        } catch (e: Exception) {
            Log.e("MainActivity", "Critical error in onCreate", e)
            // Try to show a minimal UI if everything fails
            try {
                setContentView(R.layout.activity_main)
            } catch (ex: Exception) {
                Log.e("MainActivity", "Failed to set basic content view", ex)
            }
            try {
                setContentView(R.layout.activity_main)
                supportActionBar?.hide()
            } catch (ex: Exception) {
                Log.e("MainActivity", "Critical error in onCreate", ex)
            }
        }
    }

    private fun initViews() {
        try {
            tvUserName = findViewById(R.id.tvUserName)
            tvCoinBalanceHeader = findViewById(R.id.tvCoinBalance)
            ivUserAvatar = findViewById(R.id.ivUserAvatar)
            rvSubjects = findViewById(R.id.rvSubjects)
            progressBar = findViewById(R.id.progressBar)
            layoutEmptyState = findViewById(R.id.layoutEmptyState)
            
            // Setup retry button
            val btnRetryLoad = findViewById<android.widget.Button>(R.id.btnRetryLoad)
            btnRetryLoad?.setOnClickListener {
                Log.d("MainActivity", "Retry button clicked")
                loadSubjects()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing views", e)
            throw e // Re-throw to be caught by caller
        }
    }

    private fun setupHeaderActions() {
        try {
            ivUserAvatar?.setOnClickListener {
                try {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error launching ProfileActivity", e)
                    Toast.makeText(this, "Tidak dapat membuka halaman profil", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting up header actions", e)
        }
    }

    private fun updateUserInfo() {
        try {
            // Get username from Firebase Auth or SharedPreferences
            val currentUser = firebaseAuth.currentUser
            val userName = if (currentUser != null) {
                // Get from Firebase Auth
                currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User"
            } else {
                // Get from SharedPreferences (for local users)
                sharedPreferences.getString("user_name", null) 
                    ?: sharedPreferences.getString("user_email", "")?.substringBefore("@") 
                    ?: "User"
            }
            
            tvUserName?.text = userName
            
            // Load profile image with Glide
            val profileImageUrl = currentUser?.photoUrl?.toString() 
                ?: sharedPreferences.getString("profile_image_url", "")
            
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(ivUserAvatar!!)
                
        } catch (e: Exception) {
            Log.e("MainActivity", "Error updating user info", e)
            // Set default values if update fails
            try {
                tvUserName?.text = "User"
                ivUserAvatar?.setImageResource(R.drawable.ic_person)
            } catch (e2: Exception) {
                Log.e("MainActivity", "Error setting default user info", e2)
            }
        }
    }
    
    private fun setupSubjectsRecyclerView() {
        try {
            subjectsAdapter = SubjectsAdapter { subject ->
                Log.d("MainActivity", "Subject clicked: ${subject.name}")
                onSubjectClick(subject)
            }
            
            rvSubjects.apply {
                layoutManager = GridLayoutManager(this@MainActivity, 2)
                adapter = subjectsAdapter
            }
            Log.d("MainActivity", "SubjectsRecyclerView setup completed")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error setting up subjects RecyclerView", e)
        }
    }
    
    private fun observeViewModel() {
        // Temporary: Load static subjects data
        loadSubjects()
    }
    
    private fun loadSubjects() {
        try {
            // Show loading
            progressBar.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            rvSubjects.visibility = View.GONE
            
            Log.d("MainActivity", "Starting to load subjects...")
            
            // Load subjects from Firebase
            lifecycleScope.launch {
                try {
                    Log.d("MainActivity", "Loading subjects from Firebase")
                    val result = quizRepository.getSubjects()
                    
                    if (result.isSuccess) {
                        val subjects = result.getOrNull() ?: emptyList()
                        
                        Log.d("MainActivity", "Firebase result: ${subjects.size} subjects found")
                        
                        if (subjects.isNotEmpty()) {
                            // Hide loading and show data
                            progressBar.visibility = View.GONE
                            layoutEmptyState.visibility = View.GONE
                            rvSubjects.visibility = View.VISIBLE
                            subjectsAdapter.updateSubjects(subjects)
                            
                            Log.d("MainActivity", "Successfully loaded ${subjects.size} subjects from Firebase")
                        } else {
                            // No subjects found, try to load sample data
                            Log.w("MainActivity", "No subjects found in Firebase, loading sample data")
                            loadSampleSubjects()
                        }
                    } else {
                        // Error loading from Firebase, load sample data as fallback
                        val error = result.exceptionOrNull()
                        Log.w("MainActivity", "Failed to load subjects from Firebase: ${error?.message}, using sample data")
                        loadSampleSubjects()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error loading subjects from Firebase", e)
                    // Load sample data as fallback
                    loadSampleSubjects()
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in loadSubjects", e)
            // Show empty state with retry option
            progressBar.visibility = View.GONE
            rvSubjects.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        }
    }
    
    private fun loadSampleSubjects() {
        try {
            Log.d("MainActivity", "Loading sample subjects as fallback")
            
            val sampleSubjects = listOf(
                Subject("matematika", "Matematika", "Pelajari konsep matematika dasar hingga lanjutan dengan soal-soal yang menarik", 50),
                Subject("bahasa_indonesia", "Bahasa Indonesia", "Pelajari tata bahasa dan sastra Indonesia dengan mudah dan menyenangkan", 45),
                Subject("ipa", "IPA", "Jelajahi dunia ilmu pengetahuan alam melalui eksperimen virtual", 60),
                Subject("ips", "IPS", "Pahami sejarah, geografi, dan sosiologi dengan cara yang interaktif", 40),
                Subject("bahasa_inggris", "Bahasa Inggris", "Tingkatkan kemampuan bahasa Inggris dengan latihan yang efektif", 35),
                Subject("fisika", "Fisika", "Konsep fisika yang dijelaskan dengan simulasi menarik", 55),
                Subject("kimia", "Kimia", "Eksplorasi dunia molekul dan reaksi kimia", 48),
                Subject("biologi", "Biologi", "Pelajari kehidupan dari tingkat sel hingga ekosistem", 52)
            )
            
            // Hide loading and show data
            progressBar.visibility = View.GONE
            layoutEmptyState.visibility = View.GONE
            rvSubjects.visibility = View.VISIBLE
            subjectsAdapter.updateSubjects(sampleSubjects)
            
            Log.d("MainActivity", "Successfully loaded ${sampleSubjects.size} sample subjects")
            
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading sample subjects", e)
            // Show empty state with retry option
            progressBar.visibility = View.GONE
            rvSubjects.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        }
    }
    
    private fun onSubjectClick(subject: Subject) {
        Log.d("MainActivity", "Subject clicked: ${subject.name}")
        
        try {
            // Navigate to Difficulty Selection Activity
            val intent = Intent(this, com.komputerkit.earningapp.ui.quiz.DifficultySelectionActivity::class.java)
            intent.putExtra("subject", subject)
            startActivity(intent)
            
            Log.d("MainActivity", "Navigating to DifficultySelectionActivity for subject: ${subject.name}")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error starting DifficultySelectionActivity", e)
            Toast.makeText(this, "Error memulai quiz: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCoinBalance() {
        try {
            // Use CoinManager for consistent coin loading
            lifecycleScope.launch {
                try {
                    coinBalance = coinManager.loadCoins()
                    tvCoinBalanceHeader?.text = coinBalance.toString()
                    Log.d("MainActivity", "Coins loaded via CoinManager: $coinBalance")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error loading coins via CoinManager", e)
                    // Fallback to default value
                    coinBalance = 100
                    tvCoinBalanceHeader?.text = "100"
                    Log.d("MainActivity", "Using default coin balance: 100")
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error updating coin balance", e)
            // Set default value if update fails
            try {
                coinBalance = 100
                tvCoinBalanceHeader?.text = "100"
            } catch (e2: Exception) {
                Log.e("MainActivity", "Error setting default coin balance", e2)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUserInfo()
        updateCoinBalance()
        
        // Refresh coin display to ensure consistency after returning from other activities
        try {
            lifecycleScope.launch {
                val currentCoins = coinManager.loadCoins()
                tvCoinBalanceHeader?.text = currentCoins.toString()
                Log.d("MainActivity", "Coin display refreshed on resume: $currentCoins")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error refreshing coin display on resume", e)
        }
    }
}