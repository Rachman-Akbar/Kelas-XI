package com.komputerkit.listdemoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.listdemoapp.ui.theme.ListDemoAppTheme

// Data class untuk item list
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListDemoAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = listOf(
        "List Demo",
        "LazyColumn Examples", 
        "State Management",
        "Layout Examples"
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Jetpack Compose Demo",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            val icon = when (index) {
                                0 -> Icons.Default.List
                                1 -> Icons.Default.Menu // Replacing ViewList
                                2 -> Icons.Default.Settings
                                else -> Icons.Default.Home // Replacing Dashboard
                            }
                            Icon(icon, contentDescription = title)
                        },
                        label = { Text(title, fontSize = 10.sp) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> ListDemoScreen(modifier = Modifier.padding(innerPadding))
            1 -> LazyColumnExamples(modifier = Modifier.padding(innerPadding))
            2 -> StateManagementExample(modifier = Modifier.padding(innerPadding))
            3 -> BasicLayoutExamples(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun ListDemoScreen(modifier: Modifier = Modifier) {
    // State management untuk selected item
    var selectedUserId by remember { mutableIntStateOf(-1) }
    
    // Sample data
    val userList = remember {
        listOf(
            User(1, "Ahmad Akbar", "ahmad@email.com", "Developer"),
            User(2, "Siti Nurhaliza", "siti@email.com", "Designer"),
            User(3, "Budi Santoso", "budi@email.com", "Manager"),
            User(4, "Citra Dewi", "citra@email.com", "Analyst"),
            User(5, "Doni Saputra", "doni@email.com", "Tester"),
            User(6, "Eka Pratama", "eka@email.com", "Developer"),
            User(7, "Fitri Handayani", "fitri@email.com", "Designer"),
            User(8, "Gilang Ramadhan", "gilang@email.com", "Manager"),
            User(9, "Hana Safira", "hana@email.com", "Analyst"),
            User(10, "Indra Kusuma", "indra@email.com", "Developer")
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header dengan informasi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Total Users: ${userList.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (selectedUserId != -1) {
                    val selectedUser = userList.find { it.id == selectedUserId }
                    Text(
                        text = "Selected: ${selectedUser?.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // LazyColumn dengan itemsIndexed
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(userList) { index, user ->
                UserItem(
                    user = user,
                    index = index,
                    isSelected = user.id == selectedUserId,
                    onClick = { 
                        selectedUserId = if (selectedUserId == user.id) -1 else user.id
                    }
                )
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index number
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimary 
                    else 
                        MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // User info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Contoh sederhana dengan items(count)
@Composable
fun SimpleListExample(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = 20) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle click */ },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Item #${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ListDemoAppTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ListDemoScreenPreview() {
    ListDemoAppTheme {
        ListDemoScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun UserItemPreview() {
    ListDemoAppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UserItem(
                user = User(1, "Ahmad Akbar", "ahmad@email.com", "Developer"),
                index = 0,
                isSelected = false,
                onClick = {}
            )
            UserItem(
                user = User(2, "Siti Nurhaliza", "siti@email.com", "Designer"),
                index = 1,
                isSelected = true,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SimpleListPreview() {
    ListDemoAppTheme {
        SimpleListExample()
    }
}