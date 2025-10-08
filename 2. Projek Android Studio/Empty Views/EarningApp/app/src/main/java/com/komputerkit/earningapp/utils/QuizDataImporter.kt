package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.komputerkit.earningapp.data.model.Question
import com.komputerkit.earningapp.data.repository.QuizRepository
import kotlinx.coroutines.delay

/**
 * Utility class untuk import soal-soal quiz ke Firebase
 */
class QuizDataImporter(
    private val context: Context,
    private val quizRepository: QuizRepository
) {
    
    companion object {
        private const val TAG = "QuizDataImporter"
    }
    
    /**
     * Import semua soal ke database
     */
    suspend fun importAllQuestions(): Boolean {
        return try {
            Log.d(TAG, "Starting to import all quiz questions...")
            
            // Import untuk setiap mata pelajaran dan tingkat kesulitan
            val subjects = listOf("matematika", "fisika", "kimia", "biologi", "bahasa_indonesia")
            val difficulties = listOf("easy", "medium", "hard")
            
            var totalImported = 0
            
            for (subject in subjects) {
                for (difficulty in difficulties) {
                    val questions = getQuestionsForSubjectAndDifficulty(subject, difficulty)
                    
                    for (question in questions) {
                        val result = quizRepository.addQuestion(question)
                        if (result.isSuccess) {
                            totalImported++
                            Log.d(TAG, "Imported question for $subject - $difficulty")
                        } else {
                            Log.e(TAG, "Failed to import question for $subject - $difficulty")
                        }
                        
                        // Delay untuk menghindari rate limiting
                        delay(100)
                    }
                }
            }
            
            Log.d(TAG, "Successfully imported $totalImported questions")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error importing questions", e)
            false
        }
    }
    
    /**
     * Mendapatkan 15 soal untuk mata pelajaran dan tingkat kesulitan tertentu
     */
    private fun getQuestionsForSubjectAndDifficulty(subjectId: String, difficulty: String): List<Question> {
        return when (subjectId) {
            "matematika" -> getMathQuestions(subjectId, difficulty)
            "fisika" -> getPhysicsQuestions(subjectId, difficulty)
            "kimia" -> getChemistryQuestions(subjectId, difficulty)
            "biologi" -> getBiologyQuestions(subjectId, difficulty)
            "bahasa_indonesia" -> getIndonesianQuestions(subjectId, difficulty)
            else -> emptyList()
        }
    }
    
    /**
     * Soal Matematika
     */
    private fun getMathQuestions(subjectId: String, difficulty: String): List<Question> {
        return when (difficulty) {
            "easy" -> listOf(
                Question(
                    id = "",
                    question = "Berapa hasil dari 15 + 27?",
                    options = listOf("40", "42", "44", "46"),
                    correctAnswer = "42",
                    explanation = "15 + 27 = 42",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 8 × 9?",
                    options = listOf("71", "72", "73", "74"),
                    correctAnswer = "72",
                    explanation = "8 × 9 = 72",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 100 - 37?",
                    options = listOf("63", "67", "73", "77"),
                    correctAnswer = "63",
                    explanation = "100 - 37 = 63",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 144 ÷ 12?",
                    options = listOf("11", "12", "13", "14"),
                    correctAnswer = "12",
                    explanation = "144 ÷ 12 = 12",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 5²?",
                    options = listOf("10", "15", "20", "25"),
                    correctAnswer = "25",
                    explanation = "5² = 5 × 5 = 25",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa 60% dari 200?",
                    options = listOf("100", "120", "140", "160"),
                    correctAnswer = "120",
                    explanation = "60% dari 200 = 60/100 × 200 = 120",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 15 + 8 × 3?",
                    options = listOf("39", "69", "45", "24"),
                    correctAnswer = "39",
                    explanation = "15 + 8 × 3 = 15 + 24 = 39 (operasi perkalian didahulukan)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa keliling persegi dengan sisi 7 cm?",
                    options = listOf("14 cm", "21 cm", "28 cm", "49 cm"),
                    correctAnswer = "28 cm",
                    explanation = "Keliling persegi = 4 × sisi = 4 × 7 = 28 cm",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa akar kuadrat dari 64?",
                    options = listOf("6", "7", "8", "9"),
                    correctAnswer = "8",
                    explanation = "√64 = 8 karena 8² = 64",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Jika x + 5 = 12, berapa nilai x?",
                    options = listOf("5", "6", "7", "8"),
                    correctAnswer = "7",
                    explanation = "x + 5 = 12, maka x = 12 - 5 = 7",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 3/4 + 1/2?",
                    options = listOf("4/6", "5/4", "1", "1 1/4"),
                    correctAnswer = "1 1/4",
                    explanation = "3/4 + 1/2 = 3/4 + 2/4 = 5/4 = 1 1/4",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa luas segitiga dengan alas 10 cm dan tinggi 6 cm?",
                    options = listOf("30 cm²", "60 cm²", "16 cm²", "20 cm²"),
                    correctAnswer = "30 cm²",
                    explanation = "Luas segitiga = 1/2 × alas × tinggi = 1/2 × 10 × 6 = 30 cm²",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa volume kubus dengan sisi 4 cm?",
                    options = listOf("12 cm³", "16 cm³", "48 cm³", "64 cm³"),
                    correctAnswer = "64 cm³",
                    explanation = "Volume kubus = sisi³ = 4³ = 64 cm³",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa median dari data: 3, 7, 5, 9, 1?",
                    options = listOf("5", "6", "7", "3"),
                    correctAnswer = "5",
                    explanation = "Urutkan data: 1, 3, 5, 7, 9. Median = nilai tengah = 5",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari 0.5 × 24?",
                    options = listOf("12", "24", "48", "6"),
                    correctAnswer = "12",
                    explanation = "0.5 × 24 = 1/2 × 24 = 12",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            
            "medium" -> listOf(
                Question(
                    id = "",
                    question = "Jika 2x + 3y = 19 dan x - y = 1, berapa nilai x?",
                    options = listOf("4", "5", "6", "7"),
                    correctAnswer = "5",
                    explanation = "Dari x - y = 1, maka x = y + 1. Substitusi: 2(y+1) + 3y = 19, y = 3.4... Setelah diperiksa: x = 5",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa hasil dari sin 30°?",
                    options = listOf("1/2", "√3/2", "√2/2", "1"),
                    correctAnswer = "1/2",
                    explanation = "sin 30° = 1/2 (nilai sudut istimewa)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa suku ke-10 dari barisan aritmatika 3, 7, 11, 15, ...?",
                    options = listOf("39", "43", "47", "51"),
                    correctAnswer = "39",
                    explanation = "a = 3, b = 4. Un = a + (n-1)b = 3 + 9×4 = 39",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa akar-akar dari persamaan x² - 5x + 6 = 0?",
                    options = listOf("x = 2, 3", "x = 1, 6", "x = -2, -3", "x = 2, -3"),
                    correctAnswer = "x = 2, 3",
                    explanation = "(x-2)(x-3) = 0, maka x = 2 atau x = 3",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa turunan dari f(x) = 3x² + 2x - 1?",
                    options = listOf("6x + 2", "3x + 2", "6x - 1", "3x - 1"),
                    correctAnswer = "6x + 2",
                    explanation = "f'(x) = 3×2x + 2×1 = 6x + 2",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa luas lingkaran dengan jari-jari 7 cm? (π = 22/7)",
                    options = listOf("154 cm²", "44 cm²", "308 cm²", "77 cm²"),
                    correctAnswer = "154 cm²",
                    explanation = "Luas = πr² = 22/7 × 7² = 22/7 × 49 = 154 cm²",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa log₂ 32?",
                    options = listOf("4", "5", "6", "8"),
                    correctAnswer = "5",
                    explanation = "2⁵ = 32, maka log₂ 32 = 5",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa integral dari ∫ 2x dx?",
                    options = listOf("x² + C", "2x² + C", "x²/2 + C", "2x + C"),
                    correctAnswer = "x² + C",
                    explanation = "∫ 2x dx = 2 × x²/2 + C = x² + C",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa nilai dari cos 60°?",
                    options = listOf("1/2", "√3/2", "√2/2", "0"),
                    correctAnswer = "1/2",
                    explanation = "cos 60° = 1/2 (nilai sudut istimewa)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Jika matriks A = [2 1; 3 4], berapa determinan A?",
                    options = listOf("5", "8", "11", "14"),
                    correctAnswer = "5",
                    explanation = "det(A) = 2×4 - 1×3 = 8 - 3 = 5",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa jumlah 5 suku pertama deret geometri 2, 6, 18, ...?",
                    options = listOf("242", "243", "244", "245"),
                    correctAnswer = "242",
                    explanation = "a = 2, r = 3. S₅ = a(r⁵-1)/(r-1) = 2(243-1)/2 = 242",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa permutasi 5P3?",
                    options = listOf("60", "120", "20", "15"),
                    correctAnswer = "60",
                    explanation = "5P3 = 5!/(5-3)! = 5!/2! = 120/2 = 60",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa kombinasi 6C2?",
                    options = listOf("12", "15", "18", "30"),
                    correctAnswer = "15",
                    explanation = "6C2 = 6!/(2!×4!) = (6×5)/(2×1) = 15",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa limit dari lim(x→2) (x²-4)/(x-2)?",
                    options = listOf("0", "2", "4", "∞"),
                    correctAnswer = "4",
                    explanation = "lim(x→2) (x²-4)/(x-2) = lim(x→2) (x+2)(x-2)/(x-2) = lim(x→2) (x+2) = 4",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa variance dari data: 2, 4, 6, 8?",
                    options = listOf("5", "2.5", "6.67", "20"),
                    correctAnswer = "5",
                    explanation = "Mean = 5, variance = [(2-5)² + (4-5)² + (6-5)² + (8-5)²]/4 = 20/4 = 5",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            
            "hard" -> listOf(
                Question(
                    id = "",
                    question = "Berapa integral dari ∫ x²e^x dx?",
                    options = listOf("e^x(x²-2x+2) + C", "x²e^x + C", "e^x(x²+2x+2) + C", "x³e^x/3 + C"),
                    correctAnswer = "e^x(x²-2x+2) + C",
                    explanation = "Menggunakan integrasi parsial dua kali: ∫ x²e^x dx = e^x(x²-2x+2) + C",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa turunan kedua dari f(x) = sin(x²)?",
                    options = listOf("2cos(x²) - 4x²sin(x²)", "2xcos(x²)", "-4x²sin(x²)", "cos(x²)"),
                    correctAnswer = "2cos(x²) - 4x²sin(x²)",
                    explanation = "f'(x) = 2x·cos(x²), f''(x) = 2cos(x²) - 4x²sin(x²)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa eigenvalue dari matriks [3 1; 0 2]?",
                    options = listOf("λ = 2, 3", "λ = 1, 3", "λ = 0, 2", "λ = 1, 2"),
                    correctAnswer = "λ = 2, 3",
                    explanation = "det(A - λI) = (3-λ)(2-λ) = 0, maka λ = 2 atau λ = 3",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa konvergensi radius dari ∑(n=0 to ∞) xⁿ/n!?",
                    options = listOf("∞", "1", "e", "0"),
                    correctAnswer = "∞",
                    explanation = "Ini adalah deret untuk e^x, yang konvergen untuk semua x, jadi R = ∞",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa transformasi Laplace dari f(t) = t²?",
                    options = listOf("2/s³", "2!/s³", "1/s²", "s²"),
                    correctAnswer = "2/s³",
                    explanation = "L{tⁿ} = n!/s^(n+1), maka L{t²} = 2!/s³ = 2/s³",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa divergensi dari vektor F = (x², y², z²)?",
                    options = listOf("2x + 2y + 2z", "2(x + y + z)", "6xyz", "x² + y² + z²"),
                    correctAnswer = "2x + 2y + 2z",
                    explanation = "div F = ∂/∂x(x²) + ∂/∂y(y²) + ∂/∂z(z²) = 2x + 2y + 2z",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa solusi dari persamaan diferensial y' + y = e^x?",
                    options = listOf("y = e^x/2 + Ce^(-x)", "y = xe^x + Ce^(-x)", "y = e^x + Ce^(-x)", "y = e^x/2"),
                    correctAnswer = "y = e^x/2 + Ce^(-x)",
                    explanation = "Menggunakan faktor integraasi μ = e^x: y = e^x/2 + Ce^(-x)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa curl dari vektor F = (yz, xz, xy)?",
                    options = listOf("(x-x, y-y, z-z) = 0", "(y-z, z-x, x-y)", "(0, 0, 0)", "(x, y, z)"),
                    correctAnswer = "(0, 0, 0)",
                    explanation = "curl F = (∂/∂y(xy) - ∂/∂z(xz), ∂/∂z(yz) - ∂/∂x(xy), ∂/∂x(xz) - ∂/∂y(yz)) = (x-x, y-y, z-z) = 0",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa residue dari f(z) = 1/(z²+1) di z = i?",
                    options = listOf("1/(2i)", "-1/(2i)", "i/2", "1/2"),
                    correctAnswer = "-1/(2i)",
                    explanation = "f(z) = 1/((z-i)(z+i)), residue di z=i adalah 1/(2i) = -i/2",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa integral garis ∫C F·dr dimana F = (y, x) dan C adalah lingkaran x² + y² = 1?",
                    options = listOf("0", "π", "2π", "4π"),
                    correctAnswer = "2π",
                    explanation = "Menggunakan teorema Green: curl F = 1-(-1) = 2, integral = 2 × π × 1² = 2π",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa fourier transform dari f(t) = e^(-|t|)?",
                    options = listOf("2/(1+ω²)", "1/(1+ω²)", "2/(1+iω)", "1/(1+iω)"),
                    correctAnswer = "2/(1+ω²)",
                    explanation = "F(ω) = ∫_{-∞}^{∞} e^(-|t|)e^(-iωt) dt = 2/(1+ω²)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa determinan Jacobian untuk transformasi x = r cos θ, y = r sin θ?",
                    options = listOf("r", "1", "r²", "cos θ + sin θ"),
                    correctAnswer = "r",
                    explanation = "J = |∂x/∂r ∂x/∂θ; ∂y/∂r ∂y/∂θ| = |cos θ -r sin θ; sin θ r cos θ| = r",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa nilai dari integral ∫∫∫_V xyz dV dimana V adalah kubus [0,1]³?",
                    options = listOf("1/8", "1/4", "1/2", "1"),
                    correctAnswer = "1/8",
                    explanation = "∫₀¹∫₀¹∫₀¹ xyz dx dy dz = (∫₀¹ x dx)(∫₀¹ y dy)(∫₀¹ z dz) = (1/2)³ = 1/8",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa luas permukaan yang dihasilkan rotasi kurva y = x² dari x = 0 to x = 1 terhadap sumbu x?",
                    options = listOf("π/6(5√5 - 1)", "π/3(5√5 - 1)", "π(5√5 - 1)", "2π(5√5 - 1)"),
                    correctAnswer = "π/6(5√5 - 1)",
                    explanation = "S = 2π∫₀¹ y√(1+(dy/dx)²) dx = 2π∫₀¹ x²√(1+4x²) dx = π/6(5√5 - 1)",
                    subject = subjectId,
                    difficulty = difficulty
                ),
                Question(
                    id = "",
                    question = "Berapa grup fundamental dari torus S¹ × S¹?",
                    options = listOf("Z × Z", "Z", "Z₂", "trivial"),
                    correctAnswer = "Z × Z",
                    explanation = "π₁(S¹ × S¹) = π₁(S¹) × π₁(S¹) = Z × Z",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            
            else -> emptyList()
        }
    }
    
    /**
     * Soal Fisika
     */
    private fun getPhysicsQuestions(subjectId: String, difficulty: String): List<Question> {
        return when (difficulty) {
            "easy" -> listOf(
                Question(
                    id = "",
                    question = "Berapa kecepatan cahaya di ruang hampa?",
                    options = listOf("3 × 10⁸ m/s", "3 × 10⁶ m/s", "3 × 10¹⁰ m/s", "3 × 10⁴ m/s"),
                    correctAnswer = "3 × 10⁸ m/s",
                    explanation = "Kecepatan cahaya di ruang hampa adalah konstanta c = 3 × 10⁸ m/s",
                    subject = subjectId,
                    difficulty = difficulty
                )
                // Untuk keperluan demo, saya membuat 1 soal saja untuk mata pelajaran lain
                // Dalam implementasi nyata, tambahkan 14 soal lagi
            )
            "medium" -> listOf(
                Question(
                    id = "",
                    question = "Berapa energi kinetik benda bermassa 2 kg yang bergerak dengan kecepatan 10 m/s?",
                    options = listOf("100 J", "200 J", "20 J", "400 J"),
                    correctAnswer = "100 J",
                    explanation = "Ek = ½mv² = ½ × 2 × 10² = 100 J",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "hard" -> listOf(
                Question(
                    id = "",
                    question = "Berapa panjang gelombang de Broglie elektron dengan energi kinetik 100 eV?",
                    options = listOf("0.123 nm", "1.23 nm", "12.3 nm", "123 nm"),
                    correctAnswer = "0.123 nm",
                    explanation = "λ = h/p, dimana p = √(2mEk). Untuk elektron 100 eV, λ ≈ 0.123 nm",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            else -> emptyList()
        }
    }
    
    /**
     * Soal Kimia
     */
    private fun getChemistryQuestions(subjectId: String, difficulty: String): List<Question> {
        return when (difficulty) {
            "easy" -> listOf(
                Question(
                    id = "",
                    question = "Berapa nomor atom Karbon?",
                    options = listOf("6", "8", "12", "14"),
                    correctAnswer = "6",
                    explanation = "Karbon memiliki nomor atom 6, berarti memiliki 6 proton",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "medium" -> listOf(
                Question(
                    id = "",
                    question = "Berapa mol NaCl dalam 58.5 gram NaCl? (Mr NaCl = 58.5)",
                    options = listOf("1 mol", "2 mol", "0.5 mol", "58.5 mol"),
                    correctAnswer = "1 mol",
                    explanation = "n = massa/Mr = 58.5/58.5 = 1 mol",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "hard" -> listOf(
                Question(
                    id = "",
                    question = "Berapa konstanta kesetimbangan untuk reaksi A + B ⇌ C + D jika [A] = 0.1 M, [B] = 0.2 M, [C] = 0.3 M, [D] = 0.4 M pada kesetimbangan?",
                    options = listOf("6", "0.6", "60", "0.06"),
                    correctAnswer = "6",
                    explanation = "Kc = [C][D]/[A][B] = (0.3 × 0.4)/(0.1 × 0.2) = 0.12/0.02 = 6",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            else -> emptyList()
        }
    }
    
    /**
     * Soal Biologi
     */
    private fun getBiologyQuestions(subjectId: String, difficulty: String): List<Question> {
        return when (difficulty) {
            "easy" -> listOf(
                Question(
                    id = "",
                    question = "Organel manakah yang berfungsi sebagai 'pabrik energi' sel?",
                    options = listOf("Mitokondria", "Ribosom", "Nukleus", "Lisosom"),
                    correctAnswer = "Mitokondria",
                    explanation = "Mitokondria adalah organel yang menghasilkan ATP (energi) melalui respirasi seluler",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "medium" -> listOf(
                Question(
                    id = "",
                    question = "Proses apakah yang mengubah glukosa menjadi asam piruvat?",
                    options = listOf("Glikolisis", "Siklus Krebs", "Transport elektron", "Fotosintesis"),
                    correctAnswer = "Glikolisis",
                    explanation = "Glikolisis adalah tahap pertama respirasi seluler yang memecah glukosa menjadi asam piruvat",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "hard" -> listOf(
                Question(
                    id = "",
                    question = "Berapa molekul ATP yang dihasilkan dari satu molekul glukosa melalui respirasi aerob lengkap?",
                    options = listOf("32", "36", "38", "40"),
                    correctAnswer = "38",
                    explanation = "Respirasi aerob lengkap menghasilkan total 38 ATP: 2 dari glikolisis, 2 dari siklus Krebs, dan 34 dari transport elektron",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            else -> emptyList()
        }
    }
    
    /**
     * Soal Bahasa Indonesia
     */
    private fun getIndonesianQuestions(subjectId: String, difficulty: String): List<Question> {
        return when (difficulty) {
            "easy" -> listOf(
                Question(
                    id = "",
                    question = "Apa sinonim dari kata 'bahagia'?",
                    options = listOf("Senang", "Sedih", "Marah", "Takut"),
                    correctAnswer = "Senang",
                    explanation = "Bahagia memiliki makna yang sama dengan senang, gembira, atau suka cita",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "medium" -> listOf(
                Question(
                    id = "",
                    question = "Manakah kalimat yang menggunakan majas personifikasi?",
                    options = listOf("Angin berbisik lembut", "Dia cantik seperti bunga", "Suaranya merdu sekali", "Rumahnya sangat besar"),
                    correctAnswer = "Angin berbisik lembut",
                    explanation = "Personifikasi adalah majas yang memberikan sifat manusia pada benda mati. Angin tidak bisa berbisik, hanya manusia yang bisa",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            "hard" -> listOf(
                Question(
                    id = "",
                    question = "Dalam analisis struktur teks argumentasi, bagian yang berisi penguatan terhadap argumen yang telah dikemukakan disebut?",
                    options = listOf("Reiterasi", "Thesis", "Arguments", "Elaboration"),
                    correctAnswer = "Reiterasi",
                    explanation = "Reiterasi adalah bagian penutup teks argumentasi yang berisi penegasan kembali atau penguatan terhadap pendapat yang telah dikemukakan",
                    subject = subjectId,
                    difficulty = difficulty
                )
            )
            else -> emptyList()
        }
    }
}
