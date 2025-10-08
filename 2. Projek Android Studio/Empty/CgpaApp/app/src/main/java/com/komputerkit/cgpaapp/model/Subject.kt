package com.komputerkit.cgpaapp.model

import com.komputerkit.cgpaapp.utils.GradeUtils

data class Subject(
    val name: String = "",
    val grade: String = "",
    val credit: Int = 0
) {
    fun getGradePoint(): Double {
        return GradeUtils.getGradePoint(grade)
    }
    
    fun getTotalPoints(): Double {
        return getGradePoint() * credit
    }
}

data class CGPACalculation(
    val semesterGPA: Double = 0.0,
    val cumulativeGPA: Double = 0.0,
    val totalCredits: Int = 0,
    val totalPoints: Double = 0.0
)
