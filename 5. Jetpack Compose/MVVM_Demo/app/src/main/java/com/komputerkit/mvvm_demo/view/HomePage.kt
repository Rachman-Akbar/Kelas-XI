package com.komputerkit.mvvm_demo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.mvvm_demo.viewmodel.HomeViewModel

/**
 * Home page composable that displays user data
 * @param viewModel The HomeViewModel instance to observe data from
 * @param modifier Modifier for styling the composable
 */
@Composable
fun HomePage(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // Observe user data from ViewModel
    val userData by viewModel.userData.observeAsState()
    // Observe loading state from ViewModel
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    
    // Main container column centered on screen
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Show loading indicator when data is being fetched
        if (isLoading) {
            CircularProgressIndicator()
            Text(
                text = "Loading...",
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            // Show button to fetch data when not loading
            Button(
                onClick = { viewModel.getUserData() },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Get Data")
            }
            
            // Display user data if available
            userData?.let { user ->
                Text(
                    text = "Name: ${user.name}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                Text(
                    text = "Age: ${user.age}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}