package com.komputerkit.listdemoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.komputerkit.listdemoapp.ui.theme.ListDemoAppTheme

/**
 * Contoh pengelolaan state dalam Compose
 * Menggunakan remember dan mutableStateOf
 */
@Composable
fun StateManagementExample(modifier: Modifier = Modifier) {
    // State untuk counter
    var counter by remember { mutableIntStateOf(0) }
    
    // State untuk text input
    var textInput by remember { mutableStateOf("") }
    
    // State untuk list items
    var todoList by remember { mutableStateOf(listOf<String>()) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Counter example
        CounterSection(
            counter = counter,
            onIncrement = { counter++ },
            onDecrement = { counter-- },
            onReset = { counter = 0 }
        )
        
        Divider()
        
        // Text input example
        TextInputSection(
            text = textInput,
            onTextChange = { textInput = it },
            onAddToList = {
                if (textInput.isNotBlank()) {
                    todoList = todoList + textInput
                    textInput = ""
                }
            }
        )
        
        Divider()
        
        // Todo list display
        TodoListSection(
            todos = todoList,
            onRemoveItem = { index ->
                todoList = todoList.filterIndexed { i, _ -> i != index }
            }
        )
    }
}

@Composable
fun CounterSection(
    counter: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Counter Example",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$counter",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onDecrement) {
                    Text("-")
                }
                OutlinedButton(onClick = onReset) {
                    Text("Reset")
                }
                Button(onClick = onIncrement) {
                    Text("+")
                }
            }
        }
    }
}

@Composable
fun TextInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    onAddToList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add Todo Item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("Todo item") },
                    modifier = Modifier.weight(1f)
                )
                
                Button(
                    onClick = onAddToList,
                    enabled = text.isNotBlank()
                ) {
                    Text("Add")
                }
            }
        }
    }
}

@Composable
fun TodoListSection(
    todos: List<String>,
    onRemoveItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Todo List (${todos.size} items)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (todos.isEmpty()) {
                Text(
                    text = "No items yet. Add some todos above!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                todos.forEachIndexed { index, todo ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}. $todo",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        TextButton(
                            onClick = { onRemoveItem(index) }
                        ) {
                            Text("Remove", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateManagementExamplePreview() {
    ListDemoAppTheme {
        StateManagementExample()
    }
}

@Preview(showBackground = true)
@Composable
fun CounterSectionPreview() {
    ListDemoAppTheme {
        CounterSection(
            counter = 5,
            onIncrement = {},
            onDecrement = {},
            onReset = {}
        )
    }
}