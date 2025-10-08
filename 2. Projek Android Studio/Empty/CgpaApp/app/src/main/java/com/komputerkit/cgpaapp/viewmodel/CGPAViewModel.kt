package com.komputerkit.cgpaapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.komputerkit.cgpaapp.model.CGPACalculation
import com.komputerkit.cgpaapp.model.Subject

class CGPAViewModel : ViewModel() {
    
    private val _subjects = mutableStateListOf<Subject>()
    val subjects: List<Subject> = _subjects
    
    private val _cgpaCalculation = mutableStateOf(CGPACalculation())
    val cgpaCalculation = _cgpaCalculation
    
    private val _previousCGPA = mutableStateOf(0.0)
    val previousCGPA = _previousCGPA
    
    private val _previousCredits = mutableStateOf(0)
    val previousCredits = _previousCredits
    
    fun addSubject(subject: Subject) {
        if (subject.name.isNotBlank() && subject.grade.isNotBlank() && subject.credit > 0) {
            _subjects.add(subject)
        }
    }
    
    fun removeSubject(index: Int) {
        if (index in 0 until _subjects.size) {
            _subjects.removeAt(index)
        }
    }
    
    fun updatePreviousData(cgpa: Double, credits: Int) {
        _previousCGPA.value = cgpa
        _previousCredits.value = credits
    }
    
    fun calculateCGPA() {
        val totalCredits = _subjects.sumOf { it.credit }
        val totalPoints = _subjects.sumOf { it.getTotalPoints() }
        
        val semesterGPA = if (totalCredits > 0) totalPoints / totalCredits else 0.0
        
        // Calculate cumulative GPA
        val previousTotalPoints = _previousCGPA.value * _previousCredits.value
        val combinedCredits = totalCredits + _previousCredits.value
        val combinedPoints = totalPoints + previousTotalPoints
        
        val cumulativeGPA = if (combinedCredits > 0) combinedPoints / combinedCredits else 0.0
        
        _cgpaCalculation.value = CGPACalculation(
            semesterGPA = semesterGPA,
            cumulativeGPA = cumulativeGPA,
            totalCredits = totalCredits,
            totalPoints = totalPoints
        )
    }
    
    fun clearSubjects() {
        _subjects.clear()
        _cgpaCalculation.value = CGPACalculation()
    }
}
