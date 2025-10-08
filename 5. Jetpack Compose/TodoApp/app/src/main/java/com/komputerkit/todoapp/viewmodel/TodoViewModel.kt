package com.komputerkit.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.komputerkit.todoapp.data.entity.TodoItem
import com.komputerkit.todoapp.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    
    val todos = repository.getAllTodos()
    
    private val _newTodoTitle = MutableStateFlow("")
    val newTodoTitle: StateFlow<String> = _newTodoTitle.asStateFlow()
    
    fun updateNewTodoTitle(title: String) {
        _newTodoTitle.value = title
    }
    
    fun addTodo() {
        val title = _newTodoTitle.value.trim()
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                val newTodo = TodoItem(
                    title = title,
                    createdAt = Date()
                )
                repository.insertTodo(newTodo)
                _newTodoTitle.value = ""
            }
        }
    }
    
    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
    
    fun deleteTodoById(todoId: Long) {
        viewModelScope.launch {
            repository.deleteTodoById(todoId)
        }
    }
}

class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}