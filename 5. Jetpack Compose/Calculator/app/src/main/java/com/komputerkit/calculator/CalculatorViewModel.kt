package com.komputerkit.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {
    
    private val _equation = MutableLiveData("")
    val equation: LiveData<String> = _equation
    
    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result
    
    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> clearAll()
            "C" -> clearLast()
            "=" -> calculateResult()
            else -> addToEquation(button)
        }
    }
    
    private fun clearAll() {
        _equation.value = ""
        _result.value = "0"
    }
    
    private fun clearLast() {
        val currentEquation = _equation.value ?: ""
        if (currentEquation.isNotEmpty()) {
            _equation.value = currentEquation.dropLast(1)
            evaluateExpression(_equation.value ?: "")
        }
    }
    
    private fun addToEquation(value: String) {
        val currentEquation = _equation.value ?: ""
        val newEquation = currentEquation + value
        _equation.value = newEquation
        
        // Real-time calculation
        evaluateExpression(newEquation)
    }
    
    private fun calculateResult() {
        val currentEquation = _equation.value ?: ""
        if (currentEquation.isNotEmpty()) {
            val result = evaluateExpression(currentEquation)
            if (result != "Error") {
                _equation.value = result
                _result.value = result
            }
        }
    }
    
    private fun evaluateExpression(expression: String): String {
        if (expression.isEmpty()) {
            _result.value = "0"
            return "0"
        }
        
        return try {
            // Replace display operators with JavaScript operators
            val jsExpression = expression
                .replace("×", "*")
                .replace("÷", "/")
            
            val context = Context.enter()
            context.optimizationLevel = -1
            val scope: Scriptable = context.initStandardObjects()
            
            val result = context.evaluateString(scope, jsExpression, "JavaScript", 1, null)
            Context.exit()
            
            val resultString = result.toString()
            val formattedResult = formatResult(resultString)
            _result.value = formattedResult
            formattedResult
        } catch (e: Exception) {
            _result.value = "Error"
            "Error"
        }
    }
    
    private fun formatResult(result: String): String {
        return try {
            val number = result.toDouble()
            if (number == number.toLong().toDouble()) {
                // Remove .0 if it's a whole number
                number.toLong().toString()
            } else {
                // Format decimal numbers
                String.format("%.10f", number).trimEnd('0').trimEnd('.')
            }
        } catch (e: Exception) {
            result
        }
    }
}