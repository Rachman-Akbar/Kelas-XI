package com.komputerkit.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.komputerkit.todoapp.ui.screen.TodoScreen
import com.komputerkit.todoapp.ui.theme.TodoAppTheme
import com.komputerkit.todoapp.viewmodel.TodoViewModel
import com.komputerkit.todoapp.viewmodel.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val application = application as TodoApplication
                    val viewModel: TodoViewModel = viewModel(
                        factory = TodoViewModelFactory(application.repository)
                    )
                    
                    TodoScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}