package com.komputerkit.earningapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.data.model.Question
import com.komputerkit.earningapp.data.model.Subject
import kotlinx.coroutines.tasks.await

class QuizRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "QuizRepository"
        private const val SUBJECTS_COLLECTION = "subjects"
        private const val QUESTIONS_COLLECTION = "questions"
        private const val USER_PROGRESS_COLLECTION = "user_progress"
        private const val ANSWERED_CORRECTLY_COLLECTION = "answered_correctly"
        private const val QUIZ_SESSION_SIZE = 5
    }

    /**
     * Mengambil semua mata pelajaran dari Firebase
     */
    suspend fun getSubjects(): Result<List<Subject>> {
        return try {
            Log.d(TAG, "Fetching subjects from Firebase")
            
            val snapshot = firestore.collection(SUBJECTS_COLLECTION)
                .get()
                .await()
            
            val subjects = snapshot.documents.mapNotNull { document ->
                try {
                    Subject(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        description = document.getString("description") ?: "",
                        totalQuestions = document.getLong("totalQuestions")?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing subject document: ${document.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "Successfully fetched ${subjects.size} subjects")
            Result.success(subjects)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching subjects", e)
            Result.failure(e)
        }
    }

    /**
     * Mengambil pertanyaan berdasarkan mata pelajaran dan tingkat kesulitan
     */
    suspend fun getQuestionsBySubjectAndDifficulty(
        subjectId: String,
        difficulty: String,
        limit: Int = 10
    ): Result<List<Question>> {
        return try {
            Log.d(TAG, "Fetching questions for subject: $subjectId, difficulty: $difficulty")
            
            val snapshot = firestore.collection(QUESTIONS_COLLECTION)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("difficulty", difficulty)
                .whereEqualTo("isActive", true)
                .limit(limit.toLong())
                .get()
                .await()
            
            val questions = snapshot.documents.mapNotNull { document ->
                try {
                    val options = document.get("options") as? List<String> ?: emptyList()
                    val correctAnswerIndex = document.getLong("correctAnswer")?.toInt() ?: 0
                    val correctAnswer = if (correctAnswerIndex < options.size) {
                        options[correctAnswerIndex]
                    } else {
                        options.firstOrNull() ?: ""
                    }
                    
                    Question(
                        id = document.id,
                        question = document.getString("question") ?: "",
                        options = options,
                        correctAnswer = correctAnswer,
                        difficulty = document.getString("difficulty") ?: "easy",
                        subject = document.getString("subjectId") ?: "",
                        explanation = document.getString("explanation") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing question document: ${document.id}", e)
                    null
                }
            }
            
            Log.d(TAG, "Successfully fetched ${questions.size} questions")
            Result.success(questions)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions", e)
            Result.failure(e)
        }
    }

    /**
     * Mengambil semua pertanyaan untuk mata pelajaran tertentu (semua tingkat kesulitan)
     */
    suspend fun getAllQuestionsBySubject(
        subjectId: String,
        limit: Int = 30
    ): Result<Map<String, List<Question>>> {
        return try {
            Log.d(TAG, "Fetching all questions for subject: $subjectId")
            
            val snapshot = firestore.collection(QUESTIONS_COLLECTION)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("isActive", true)
                .limit(limit.toLong())
                .get()
                .await()
            
            val allQuestions = snapshot.documents.mapNotNull { document ->
                try {
                    val options = document.get("options") as? List<String> ?: emptyList()
                    val correctAnswerIndex = document.getLong("correctAnswer")?.toInt() ?: 0
                    val correctAnswer = if (correctAnswerIndex < options.size) {
                        options[correctAnswerIndex]
                    } else {
                        options.firstOrNull() ?: ""
                    }
                    
                    Question(
                        id = document.id,
                        question = document.getString("question") ?: "",
                        options = options,
                        correctAnswer = correctAnswer,
                        difficulty = document.getString("difficulty") ?: "easy",
                        subject = document.getString("subjectId") ?: "",
                        explanation = document.getString("explanation") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing question document: ${document.id}", e)
                    null
                }
            }
            
            // Grup pertanyaan berdasarkan tingkat kesulitan
            val questionsByDifficulty = allQuestions.groupBy { it.difficulty }
            
            Log.d(TAG, "Successfully fetched questions by difficulty: ${questionsByDifficulty.mapValues { it.value.size }}")
            Result.success(questionsByDifficulty)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching questions by subject", e)
            Result.failure(e)
        }
    }

    /**
     * Mengambil pertanyaan random berdasarkan mata pelajaran dan tingkat kesulitan
     */
    suspend fun getRandomQuestionsBySubjectAndDifficulty(
        subjectId: String,
        difficulty: String,
        count: Int = 10
    ): Result<List<Question>> {
        return try {
            Log.d(TAG, "Fetching random questions for subject: $subjectId, difficulty: $difficulty, count: $count")
            
            // Ambil semua pertanyaan untuk subject dan difficulty
            val allQuestionsResult = getQuestionsBySubjectAndDifficulty(subjectId, difficulty, 100)
            
            if (allQuestionsResult.isFailure) {
                return allQuestionsResult
            }
            
            val allQuestions = allQuestionsResult.getOrNull() ?: emptyList()
            
            // Ambil random questions
            val randomQuestions = if (allQuestions.size <= count) {
                allQuestions
            } else {
                allQuestions.shuffled().take(count)
            }
            
            Log.d(TAG, "Successfully selected ${randomQuestions.size} random questions")
            Result.success(randomQuestions)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching random questions", e)
            Result.failure(e)
        }
    }

    /**
     * Mengecek apakah ada pertanyaan untuk mata pelajaran dan kesulitan tertentu
     */
    suspend fun hasQuestionsForSubjectAndDifficulty(
        subjectId: String,
        difficulty: String
    ): Result<Boolean> {
        return try {
            Log.d(TAG, "Checking if questions exist for subject: $subjectId, difficulty: $difficulty")
            
            val snapshot = firestore.collection(QUESTIONS_COLLECTION)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("difficulty", difficulty)
                .whereEqualTo("isActive", true)
                .limit(1)
                .get()
                .await()
            
            val hasQuestions = !snapshot.isEmpty
            Log.d(TAG, "Questions exist: $hasQuestions")
            Result.success(hasQuestions)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking questions existence", e)
            Result.failure(e)
        }
    }

    /**
     * Mendapatkan soal acak untuk quiz session dengan mempertimbangkan soal yang sudah dijawab benar
     */
    suspend fun getRandomQuizSession(
        subjectId: String,
        difficulty: String
    ): Result<List<Question>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            Log.d(TAG, "Getting random quiz session for user: $userId, subject: $subjectId, difficulty: $difficulty")
            
            // Ambil semua soal untuk subject dan difficulty
            val allQuestionsResult = getQuestionsBySubjectAndDifficulty(subjectId, difficulty, 100)
            if (allQuestionsResult.isFailure) {
                return allQuestionsResult
            }
            
            val allQuestions = allQuestionsResult.getOrNull() ?: emptyList()
            if (allQuestions.isEmpty()) {
                return Result.failure(Exception("No questions available"))
            }
            
            // Ambil daftar soal yang sudah dijawab benar oleh user
            val answeredCorrectlyIds = getAnsweredCorrectlyQuestionIds(userId, subjectId, difficulty)
            
            // Filter soal yang belum dijawab benar
            val unansweredQuestions = allQuestions.filter { question ->
                !answeredCorrectlyIds.contains(question.id)
            }
            
            // Pilih soal untuk session
            val sessionQuestions = if (unansweredQuestions.size >= QUIZ_SESSION_SIZE) {
                // Jika ada cukup soal yang belum dijawab, pilih dari yang belum dijawab
                unansweredQuestions.shuffled().take(QUIZ_SESSION_SIZE)
            } else {
                // Jika soal yang belum dijawab tidak cukup, gabungkan dengan soal yang sudah dijawab
                val remainingNeeded = QUIZ_SESSION_SIZE - unansweredQuestions.size
                val answeredQuestions = allQuestions.filter { question ->
                    answeredCorrectlyIds.contains(question.id)
                }.shuffled().take(remainingNeeded)
                
                (unansweredQuestions + answeredQuestions).shuffled()
            }
            
            Log.d(TAG, "Selected ${sessionQuestions.size} questions for quiz session")
            Result.success(sessionQuestions)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting random quiz session", e)
            Result.failure(e)
        }
    }
    
    /**
     * Menyimpan soal yang dijawab benar oleh user
     */
    suspend fun markQuestionAsAnsweredCorrectly(
        userId: String,
        questionId: String,
        subjectId: String,
        difficulty: String
    ): Result<Boolean> {
        return try {
            val documentData = mapOf(
                "userId" to userId,
                "questionId" to questionId,
                "subjectId" to subjectId,
                "difficulty" to difficulty,
                "answeredAt" to System.currentTimeMillis()
            )
            
            firestore.collection(USER_PROGRESS_COLLECTION)
                .document(userId)
                .collection(ANSWERED_CORRECTLY_COLLECTION)
                .document("${subjectId}_${difficulty}_${questionId}")
                .set(documentData)
                .await()
                
            Log.d(TAG, "Marked question $questionId as answered correctly for user $userId")
            Result.success(true)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error marking question as answered correctly", e)
            Result.failure(e)
        }
    }
    
    /**
     * Mengambil ID soal yang sudah dijawab benar oleh user
     */
    private suspend fun getAnsweredCorrectlyQuestionIds(
        userId: String,
        subjectId: String,
        difficulty: String
    ): Set<String> {
        return try {
            val snapshot = firestore.collection(USER_PROGRESS_COLLECTION)
                .document(userId)
                .collection(ANSWERED_CORRECTLY_COLLECTION)
                .whereEqualTo("subjectId", subjectId)
                .whereEqualTo("difficulty", difficulty)
                .get()
                .await()
                
            val questionIds = snapshot.documents.mapNotNull { document ->
                document.getString("questionId")
            }.toSet()
            
            Log.d(TAG, "Found ${questionIds.size} questions answered correctly by user $userId")
            questionIds
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting answered correctly question IDs", e)
            emptySet()
        }
    }

    /**
     * Menambahkan soal baru ke database
     */
    suspend fun addQuestion(question: Question): Result<String> {
        return try {
            val questionData = mapOf(
                "question" to question.question,
                "options" to question.options,
                "correctAnswer" to question.correctAnswer,
                "explanation" to question.explanation,
                "subject" to question.subject,
                "difficulty" to question.difficulty,
                "isActive" to true,
                "createdAt" to System.currentTimeMillis()
            )
            
            val documentRef = firestore.collection(QUESTIONS_COLLECTION).add(questionData).await()
            
            Log.d(TAG, "Successfully added question with ID: ${documentRef.id}")
            Result.success(documentRef.id)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding question", e)
            Result.failure(e)
        }
    }
}
