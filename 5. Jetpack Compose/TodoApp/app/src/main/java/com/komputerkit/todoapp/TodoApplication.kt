package com.komputerkit.todoapp

import android.app.Application
import com.komputerkit.todoapp.data.database.TodoDatabase
import com.komputerkit.todoapp.repository.TodoRepository

class TodoApplication : Application() {
    
    val database by lazy { TodoDatabase.getDatabase(this) }
    val repository by lazy { TodoRepository(database.todoDao()) }
}