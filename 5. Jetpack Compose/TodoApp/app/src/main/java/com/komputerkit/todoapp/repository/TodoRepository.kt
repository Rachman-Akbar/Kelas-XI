package com.komputerkit.todoapp.repository

import com.komputerkit.todoapp.data.dao.TodoDao
import com.komputerkit.todoapp.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    
    fun getAllTodos(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos()
    }
    
    suspend fun insertTodo(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }
    
    suspend fun deleteTodo(todo: TodoItem) {
        todoDao.deleteTodo(todo)
    }
    
    suspend fun deleteTodoById(todoId: Long) {
        todoDao.deleteTodoById(todoId)
    }
}