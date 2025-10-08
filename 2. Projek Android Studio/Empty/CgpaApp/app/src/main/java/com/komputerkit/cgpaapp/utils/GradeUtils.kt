package com.komputerkit.cgpaapp.utils

object GradeUtils {
    
    private val gradeToPointMap = mapOf(
        "A" to 4.0,
        "A-" to 3.7,
        "B+" to 3.3,
        "B" to 3.0,
        "B-" to 2.7,
        "C+" to 2.3,
        "C" to 2.0,
        "C-" to 1.7,
        "D+" to 1.3,
        "D" to 1.0,
        "E" to 0.0,
        "F" to 0.0
    )
    
    fun getGradePoint(grade: String): Double {
        return gradeToPointMap[grade.uppercase()] ?: 0.0
    }
    
    fun getAvailableGrades(): List<String> {
        return gradeToPointMap.keys.toList()
    }
    
    fun getGradeCategory(gpa: Double): String {
        return when {
            gpa >= 3.75 -> "Cum Laude"
            gpa >= 3.5 -> "Sangat Memuaskan"
            gpa >= 3.0 -> "Memuaskan"
            gpa >= 2.5 -> "Cukup"
            gpa >= 2.0 -> "Kurang"
            else -> "Sangat Kurang"
        }
    }
    
    fun formatGPA(gpa: Double): String {
        return String.format("%.2f", gpa)
    }
}
