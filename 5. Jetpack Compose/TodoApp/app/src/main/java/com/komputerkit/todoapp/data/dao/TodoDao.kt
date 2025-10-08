package com.komputerkit.todoapp.data.dao

import androidx.room.*
import com.komputerkit.todoapp.data.entity.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :todoId")
    suspend fun deleteTodoById(todoId: Long)
}