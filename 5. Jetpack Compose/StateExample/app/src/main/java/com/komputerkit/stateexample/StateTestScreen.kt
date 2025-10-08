package com.komputerkit.stateexample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StateTestScreen(viewModel: StateTestViewModel, modifier: Modifier = Modifier) {
    val name by viewModel.name.observeAsState("")
    val surname by viewModel.surname.observeAsState("")
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyText(name = name, surname = surname)
        
        MyTextField(
            value = name,
            onValueChange = viewModel::onNameUpdate,
            label = "Enter Name"
        )
        
        MyTextField(
            value = surname,
            onValueChange = viewModel::onSurnameUpdate,
            label = "Enter Surname"
        )
    }
}

@Composable
fun MyText(name: String, surname: String) {
    Text(
        text = "Hello $name $surname",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    )
}

@Composable
fun MyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}