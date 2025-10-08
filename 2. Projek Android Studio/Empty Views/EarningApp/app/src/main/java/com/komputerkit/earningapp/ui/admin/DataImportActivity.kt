package com.komputerkit.earningapp.ui.admin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.komputerkit.earningapp.databinding.ActivityDataImportBinding
import com.komputerkit.earningapp.data.repository.QuizRepository
import com.komputerkit.earningapp.utils.QuizDataImporter
import kotlinx.coroutines.launch

/**
 * Activity untuk import data soal quiz ke Firebase
 * Hanya untuk development/admin
 */
class DataImportActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDataImportBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var dataImporter: QuizDataImporter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDataImportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        quizRepository = QuizRepository()
        dataImporter = QuizDataImporter(this, quizRepository)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.apply {
            btnImportData.setOnClickListener {
                importQuizData()
            }
            
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
    
    private fun importQuizData() {
        binding.apply {
            btnImportData.isEnabled = false
            progressBar.visibility = android.view.View.VISIBLE
            tvStatus.text = "Memulai import data..."
        }
        
        lifecycleScope.launch {
            try {
                Log.d("DataImport", "Starting quiz data import...")
                
                binding.tvStatus.text = "Mengimport soal-soal quiz..."
                
                val success = dataImporter.importAllQuestions()
                
                if (success) {
                    binding.tvStatus.text = "Import berhasil! Semua soal telah ditambahkan ke database."
                    Toast.makeText(this@DataImportActivity, "Import berhasil!", Toast.LENGTH_LONG).show()
                } else {
                    binding.tvStatus.text = "Import gagal! Silakan coba lagi."
                    Toast.makeText(this@DataImportActivity, "Import gagal!", Toast.LENGTH_LONG).show()
                }
                
            } catch (e: Exception) {
                Log.e("DataImport", "Error during import", e)
                binding.tvStatus.text = "Error: ${e.message}"
                Toast.makeText(this@DataImportActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.apply {
                    btnImportData.isEnabled = true
                    progressBar.visibility = android.view.View.GONE
                }
            }
        }
    }
}
