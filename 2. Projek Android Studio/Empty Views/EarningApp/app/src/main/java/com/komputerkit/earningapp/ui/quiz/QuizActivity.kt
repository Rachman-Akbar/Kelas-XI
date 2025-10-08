package com.komputerkit.earningapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.MainActivity
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.model.Question
import com.komputerkit.earningapp.data.model.Subject
import com.komputerkit.earningapp.data.repository.QuizRepository
import com.komputerkit.earningapp.databinding.ActivityQuizBinding
import com.komputerkit.earningapp.utils.CoinManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var subject: Subject
    private lateinit var quizRepository: QuizRepository
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var score = 0
    private var selectedAnswer = ""
    private var timer: CountDownTimer? = null
    private val timePerQuestion = 30000L // 30 seconds per question
    private var selectedDifficulty = "easy" // Default difficulty
    
    // Points per correct answer based on difficulty
    private fun getPointsPerQuestion(): Int {
        return when (selectedDifficulty) {
            "easy" -> 5
            "medium" -> 10
            "hard" -> 15
            else -> 5
        }
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var coinManager: CoinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("QuizActivity", "QuizActivity started")
        
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize QuizRepository and CoinManager
        quizRepository = QuizRepository()
        coinManager = CoinManager.getInstance(this)

        // Get subject data from intent
        subject = intent.getParcelableExtra("subject") ?: run {
            Log.e("QuizActivity", "No subject data received")
            Toast.makeText(this, "Error: No subject data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Get difficulty from intent (if provided)
        selectedDifficulty = intent.getStringExtra("difficulty") ?: "easy"

        Log.d("QuizActivity", "Subject: ${subject.name}, Difficulty: $selectedDifficulty")

        // Hide action bar
        supportActionBar?.hide()

        setupUI()
        loadQuestions()
    }

    private fun setupUI() {
        binding.apply {
            tvSubjectName.text = subject.name
            tvQuestionCounter.text = "0/0"
            tvScore.text = "Score: $score"

            // Setup answer buttons
            btnAnswer1.setOnClickListener { selectAnswer(btnAnswer1.text.toString()) }
            btnAnswer2.setOnClickListener { selectAnswer(btnAnswer2.text.toString()) }
            btnAnswer3.setOnClickListener { selectAnswer(btnAnswer3.text.toString()) }
            btnAnswer4.setOnClickListener { selectAnswer(btnAnswer4.text.toString()) }

            btnNext.setOnClickListener { nextQuestion() }
            btnQuit.setOnClickListener { quitQuiz() }
            
            // Initially hide quiz layout
            layoutQuiz.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun loadQuestions() {
        Log.d("QuizActivity", "Loading quiz session for subject: ${subject.id}, difficulty: $selectedDifficulty")
        
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                binding.layoutQuiz.visibility = View.GONE

                // Load quiz session (5 questions) using new random system
                val result = quizRepository.getRandomQuizSession(
                    subjectId = subject.id,
                    difficulty = selectedDifficulty
                )

                if (result.isSuccess) {
                    questions = result.getOrNull() ?: emptyList()
                    Log.d("QuizActivity", "Loaded ${questions.size} questions for quiz session")
                } else {
                    Log.w("QuizActivity", "Failed to load quiz session, using fallback method")
                    // Fallback to old method if new system fails
                    val fallbackResult = quizRepository.getRandomQuestionsBySubjectAndDifficulty(
                        subjectId = subject.id,
                        difficulty = selectedDifficulty,
                        count = 5
                    )
                    if (fallbackResult.isSuccess) {
                        questions = fallbackResult.getOrNull()?.take(5) ?: emptyList()
                    } else {
                        questions = createSampleQuestions().take(5)
                    }
                }

                Log.d("QuizActivity", "Total questions for session: ${questions.size}")

                if (questions.isEmpty()) {
                    Log.w("QuizActivity", "No questions found, showing error")
                    showError("Tidak ada pertanyaan tersedia untuk mata pelajaran ${subject.name}")
                } else {
                    Log.d("QuizActivity", "Starting quiz session with ${questions.size} questions")
                    binding.progressBar.visibility = View.GONE
                    binding.layoutQuiz.visibility = View.VISIBLE
                    displayQuestion()
                }

            } catch (e: Exception) {
                Log.e("QuizActivity", "Error loading quiz session", e)
                binding.progressBar.visibility = View.GONE
                questions = createSampleQuestions()
                if (questions.isNotEmpty()) {
                    startQuiz()
                } else {
                    showError("Tidak ada pertanyaan tersedia untuk mata pelajaran ini")
                }
            }
        }
    }

    private fun createSampleQuestions(): List<Question> {
        Log.d("QuizActivity", "Creating sample questions for ${subject.name}, difficulty: $selectedDifficulty")
        
        return when (subject.name.lowercase()) {
            "matematika" -> createMathQuestions()
            "bahasa indonesia" -> createIndonesianQuestions()
            "ipa" -> createScienceQuestions()
            "ips" -> createSocialQuestions()
            "bahasa inggris" -> createEnglishQuestions()
            else -> createGeneralQuestions()
        }.filter { it.difficulty == selectedDifficulty }.take(10)
    }
    
    private fun createMathQuestions(): List<Question> {
        return listOf(
            // Easy questions
            Question("math_1", "Berapa hasil dari 2 + 2?", listOf("3", "4", "5", "6"), "4", "easy", subject.id),
            Question("math_2", "Berapa hasil dari 5 × 3?", listOf("12", "15", "18", "20"), "15", "easy", subject.id),
            Question("math_3", "Berapa hasil dari 10 ÷ 2?", listOf("3", "4", "5", "6"), "5", "easy", subject.id),
            Question("math_4", "Berapa hasil dari 7 + 8?", listOf("14", "15", "16", "17"), "15", "easy", subject.id),
            Question("math_5", "Berapa hasil dari 6 × 2?", listOf("10", "12", "14", "16"), "12", "easy", subject.id),
            
            // Medium questions
            Question("math_6", "Berapa hasil dari 15 × 4?", listOf("60", "65", "70", "75"), "60", "medium", subject.id),
            Question("math_7", "Berapa hasil dari 144 ÷ 12?", listOf("10", "11", "12", "13"), "12", "medium", subject.id),
            Question("math_8", "Berapa hasil dari 23 + 47?", listOf("68", "70", "72", "74"), "70", "medium", subject.id),
            Question("math_9", "Berapa akar kuadrat dari 64?", listOf("6", "7", "8", "9"), "8", "medium", subject.id),
            Question("math_10", "Berapa hasil dari 8²?", listOf("62", "64", "66", "68"), "64", "medium", subject.id),
            
            // Hard questions
            Question("math_11", "Berapa hasil dari 17 × 23?", listOf("381", "391", "401", "411"), "391", "hard", subject.id),
            Question("math_12", "Berapa hasil dari ³√125?", listOf("3", "4", "5", "6"), "5", "hard", subject.id),
            Question("math_13", "Jika x + 5 = 12, berapa nilai x?", listOf("5", "6", "7", "8"), "7", "hard", subject.id),
            Question("math_14", "Berapa nilai dari sin 90°?", listOf("0", "0.5", "1", "1.5"), "1", "hard", subject.id)
        )
    }
    
    private fun createIndonesianQuestions(): List<Question> {
        return listOf(
            // Easy questions
            Question("indo_1", "Apa ibu kota Indonesia?", listOf("Bandung", "Jakarta", "Surabaya", "Medan"), "Jakarta", "easy", subject.id),
            Question("indo_2", "Apa nama lain dari bahasa Indonesia?", listOf("Bahasa Melayu", "Bahasa Jawa", "Bahasa Sunda", "Bahasa Bali"), "Bahasa Melayu", "easy", subject.id),
            Question("indo_3", "Berapa jumlah suku kata dalam 'Indonesia'?", listOf("3", "4", "5", "6"), "4", "easy", subject.id),
            Question("indo_4", "Apa arti kata 'Bhinneka'?", listOf("Berbeda", "Sama", "Indah", "Besar"), "Berbeda", "easy", subject.id),
            
            // Medium questions
            Question("indo_5", "Siapa penulis novel Laskar Pelangi?", listOf("Andrea Hirata", "Pramoedya", "Chairil Anwar", "Taufik Ismail"), "Andrea Hirata", "medium", subject.id),
            Question("indo_6", "Kapan Hari Bahasa Nasional?", listOf("17 Agustus", "28 Oktober", "1 Juni", "20 Mei"), "28 Oktober", "medium", subject.id),
            Question("indo_7", "Apa nama puisi karya Chairil Anwar yang terkenal?", listOf("Aku", "Diponegoro", "Karawang Bekasi", "Gadis Peminta-minta"), "Aku", "medium", subject.id),
            
            // Hard questions
            Question("indo_8", "Siapa penulis novel Bumi Manusia?", listOf("Pramoedya Ananta Toer", "Mochtar Lubis", "Ahmad Tohari", "Rendra"), "Pramoedya Ananta Toer", "hard", subject.id)
        )
    }
    
    private fun createScienceQuestions(): List<Question> {
        return listOf(
            // Easy questions
            Question("ipa_1", "Planet terdekat dengan matahari?", listOf("Venus", "Merkurius", "Bumi", "Mars"), "Merkurius", "easy", subject.id),
            Question("ipa_2", "Apa simbol kimia untuk air?", listOf("H2O", "CO2", "O2", "H2"), "H2O", "easy", subject.id),
            Question("ipa_3", "Organ mana yang memompa darah?", listOf("Paru-paru", "Jantung", "Hati", "Ginjal"), "Jantung", "easy", subject.id),
            Question("ipa_4", "Berapa jumlah kaki pada laba-laba?", listOf("6", "8", "10", "12"), "8", "easy", subject.id),
            
            // Medium questions
            Question("ipa_5", "Berapa jumlah kromosom manusia?", listOf("44", "46", "48", "50"), "46", "medium", subject.id),
            Question("ipa_6", "Apa gas yang dihasilkan fotosintesis?", listOf("CO2", "O2", "N2", "H2"), "O2", "medium", subject.id),
            Question("ipa_7", "Planet terbesar di tata surya?", listOf("Saturnus", "Jupiter", "Uranus", "Neptunus"), "Jupiter", "medium", subject.id),
            
            // Hard questions
            Question("ipa_8", "Apa nama ilmiah manusia?", listOf("Homo sapiens", "Homo erectus", "Homo habilis", "Homo rudolfensis"), "Homo sapiens", "hard", subject.id)
        )
    }
    
    private fun createSocialQuestions(): List<Question> {
        return listOf(
            // Easy questions
            Question("ips_1", "Siapa presiden pertama Indonesia?", listOf("Soekarno", "Soeharto", "Habibie", "Megawati"), "Soekarno", "easy", subject.id),
            Question("ips_2", "Kapan Indonesia merdeka?", listOf("17 Agustus 1945", "17 Agustus 1944", "17 Agustus 1946", "17 Agustus 1947"), "17 Agustus 1945", "easy", subject.id),
            Question("ips_3", "Apa nama mata uang Indonesia?", listOf("Rupiah", "Ringgit", "Peso", "Baht"), "Rupiah", "easy", subject.id),
            
            // Medium questions
            Question("ips_4", "Siapa yang memproklamirkan kemerdekaan Indonesia?", listOf("Soekarno-Hatta", "Soeharto", "Sudirman", "Diponegoro"), "Soekarno-Hatta", "medium", subject.id),
            Question("ips_5", "Apa nama organisasi pemuda yang didirikan tahun 1908?", listOf("Budi Utomo", "Sarekat Islam", "Indische Partij", "PKI"), "Budi Utomo", "medium", subject.id),
            
            // Hard questions
            Question("ips_6", "Kapan Perang Diponegoro terjadi?", listOf("1825-1830", "1830-1835", "1820-1825", "1835-1840"), "1825-1830", "hard", subject.id)
        )
    }
    
    private fun createEnglishQuestions(): List<Question> {
        return listOf(
            // Easy questions
            Question("eng_1", "What is the capital of Indonesia?", listOf("Bandung", "Jakarta", "Surabaya", "Medan"), "Jakarta", "easy", subject.id),
            Question("eng_2", "How do you say 'Terima kasih' in English?", listOf("Thank you", "You're welcome", "Excuse me", "I'm sorry"), "Thank you", "easy", subject.id),
            Question("eng_3", "What color is the sun?", listOf("Blue", "Green", "Yellow", "Red"), "Yellow", "easy", subject.id),
            
            // Medium questions
            Question("eng_4", "What is the past tense of 'go'?", listOf("goes", "went", "gone", "going"), "went", "medium", subject.id),
            Question("eng_5", "Which is correct?", listOf("I am student", "I am a student", "I am the student", "I student"), "I am a student", "medium", subject.id),
            
            // Hard questions
            Question("eng_6", "What is the correct form: 'If I __ rich, I would travel the world'?", listOf("was", "were", "am", "is"), "were", "hard", subject.id)
        )
    }
    
    private fun createGeneralQuestions(): List<Question> {
        return listOf(
            Question("gen_1", "Berapa hasil dari 2 + 2?", listOf("3", "4", "5", "6"), "4", "easy", subject.id),
            Question("gen_2", "Apa ibu kota Indonesia?", listOf("Bandung", "Jakarta", "Surabaya", "Medan"), "Jakarta", "easy", subject.id),
            Question("gen_3", "Planet terdekat dengan matahari?", listOf("Venus", "Merkurius", "Bumi", "Mars"), "Merkurius", "easy", subject.id)
        )
    }

    private fun startQuiz() {
        if (questions.isNotEmpty()) {
            binding.progressBar.visibility = View.GONE
            binding.layoutQuiz.visibility = View.VISIBLE
            displayQuestion()
        } else {
            showError("Tidak ada pertanyaan tersedia")
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun displayQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finishQuiz()
            return
        }

        val question = questions[currentQuestionIndex]
        
        binding.apply {
            tvQuestionCounter.text = "${currentQuestionIndex + 1}/${questions.size}"
            tvQuestion.text = question.question
            
            // Ensure we have at least 4 options
            val options = if (question.options.size >= 4) {
                question.options
            } else {
                // Add default options if not enough
                val defaultOptions = mutableListOf<String>()
                defaultOptions.addAll(question.options)
                while (defaultOptions.size < 4) {
                    defaultOptions.add("Pilihan ${defaultOptions.size + 1}")
                }
                defaultOptions
            }
            
            btnAnswer1.text = options[0]
            btnAnswer2.text = options[1]
            btnAnswer3.text = options[2]
            btnAnswer4.text = options[3]

            // Reset button states
            resetAnswerButtons()
            btnNext.isEnabled = false
            selectedAnswer = ""
        }

        startTimer()
    }

    private fun selectAnswer(answer: String) {
        selectedAnswer = answer
        resetAnswerButtons()
        
        // Highlight selected answer
        binding.apply {
            when (answer) {
                btnAnswer1.text -> btnAnswer1.setBackgroundColor(getColor(R.color.selected_answer))
                btnAnswer2.text -> btnAnswer2.setBackgroundColor(getColor(R.color.selected_answer))
                btnAnswer3.text -> btnAnswer3.setBackgroundColor(getColor(R.color.selected_answer))
                btnAnswer4.text -> btnAnswer4.setBackgroundColor(getColor(R.color.selected_answer))
            }
        }
        
        binding.btnNext.isEnabled = true
    }

    private fun resetAnswerButtons() {
        val defaultColor = getColor(R.color.default_button)
        binding.apply {
            btnAnswer1.setBackgroundColor(defaultColor)
            btnAnswer2.setBackgroundColor(defaultColor)
            btnAnswer3.setBackgroundColor(defaultColor)
            btnAnswer4.setBackgroundColor(defaultColor)
        }
    }

    private fun nextQuestion() {
        timer?.cancel()
        
        val currentQuestion = questions[currentQuestionIndex]
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer
        val pointsEarned = getPointsPerQuestion()
        
        if (isCorrect) {
            score += pointsEarned
            Toast.makeText(this, "Benar! +$pointsEarned poin", Toast.LENGTH_SHORT).show()
            
            // Track correct answer for future quiz sessions
            val userId = auth.currentUser?.uid
            if (userId != null) {
                lifecycleScope.launch {
                    try {
                        quizRepository.markQuestionAsAnsweredCorrectly(
                            userId = userId,
                            questionId = currentQuestion.id,
                            subjectId = subject.id,
                            difficulty = selectedDifficulty
                        )
                        Log.d("QuizActivity", "Marked question ${currentQuestion.id} as answered correctly")
                    } catch (e: Exception) {
                        Log.e("QuizActivity", "Error marking question as answered correctly", e)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Salah! Jawaban: ${currentQuestion.correctAnswer}", Toast.LENGTH_SHORT).show()
        }
        
        binding.tvScore.text = "Score: $score"
        currentQuestionIndex++
        
        displayQuestion()
    }

    private fun startTimer() {
        timer?.cancel()
        
        timer = object : CountDownTimer(timePerQuestion, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvTimer.text = "Waktu: ${seconds}s"
            }

            override fun onFinish() {
                Toast.makeText(this@QuizActivity, "Waktu habis!", Toast.LENGTH_SHORT).show()
                nextQuestion()
            }
        }.start()
    }

    private fun finishQuiz() {
        timer?.cancel()
        
        Log.d("QuizActivity", "Quiz finished. Score: $score")
        
        // Calculate coins earned (score = coins)
        val coinsEarned = score
        
        // Save score and update coins
        saveScoreAndCoins(coinsEarned)
        
        // Show result
        val maxPossibleScore = questions.size * getPointsPerQuestion()
        val percentage = if (maxPossibleScore > 0) {
            (score.toDouble() / maxPossibleScore * 100).toInt()
        } else {
            0
        }
        
        val message = "Quiz selesai!\n" +
                "Tingkat: ${selectedDifficulty.uppercase()}\n" +
                "Score: $score/$maxPossibleScore ($percentage%)\n" +
                "Koin didapat: +$coinsEarned 🪙"
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Return to DifficultySelectionActivity after delay
        binding.layoutQuiz.postDelayed({
            val intent = Intent(this, DifficultySelectionActivity::class.java)
            intent.putExtra("subject", subject)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun saveScoreAndCoins(coinsEarned: Int) {
        val user = auth.currentUser
        lifecycleScope.launch {
            try {
                // Save score to Firebase
                if (user != null) {
                    val userDoc = firestore.collection("users").document(user.uid)
                    
                    // Save quiz score
                    val scoreData = mapOf(
                        "subject" to subject.name,
                        "difficulty" to selectedDifficulty,
                        "score" to score,
                        "totalQuestions" to questions.size,
                        "maxScore" to (questions.size * getPointsPerQuestion()),
                        "coinsEarned" to coinsEarned,
                        "timestamp" to System.currentTimeMillis()
                    )
                    
                    try {
                        userDoc.collection("quiz_scores").add(scoreData).await()
                        Log.d("QuizActivity", "Quiz score saved to Firebase")
                    } catch (e: Exception) {
                        Log.e("QuizActivity", "Error saving quiz score to Firebase", e)
                    }
                }
                
                // Update coins using CoinManager (handles both Firebase and SharedPreferences)
                try {
                    val newCoins = coinManager.addCoins(coinsEarned)
                    Log.d("QuizActivity", "Coins updated successfully. New total: $newCoins")
                } catch (e: Exception) {
                    Log.e("QuizActivity", "Error updating coins via CoinManager", e)
                    // Fallback to local save
                    val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val currentCoins = sharedPrefs.getInt("coins", 0)
                    val newCoins = currentCoins + coinsEarned
                    sharedPrefs.edit().putInt("coins", newCoins).apply()
                    Log.d("QuizActivity", "Coins saved locally as fallback. New total: $newCoins")
                }
                
            } catch (e: Exception) {
                Log.e("QuizActivity", "Error in saveScoreAndCoins", e)
            }
        }
    }

    private fun quitQuiz() {
        timer?.cancel()
        Toast.makeText(this, "Quiz dibatalkan", Toast.LENGTH_SHORT).show()
        
        // Return to DifficultySelectionActivity
        val intent = Intent(this, DifficultySelectionActivity::class.java)
        intent.putExtra("subject", subject)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        quitQuiz()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
