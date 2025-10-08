package com.komputerkit.bottomnaviigationdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.komputerkit.bottomnaviigationdemo.ui.theme.BottomNaviigationDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BottomNaviigationDemoTheme {
                BottomNavigationApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationApp() {
    var selectedIndex by remember { mutableStateOf(0) }
    
    val navItems = listOf(
        NavItem(
            label = "Beranda",
            icon = Icons.Default.Home,
            badgeCount = 0
        ),
        NavItem(
            label = "Notifikasi", 
            icon = Icons.Default.Notifications,
            badgeCount = 5 // Badge untuk menunjukkan notifikasi baru
        ),
        NavItem(
            label = "Pengaturan",
            icon = Icons.Default.Settings,
            badgeCount = 0
        )
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = {
                            if (item.badgeCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(
                                                text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString()
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = {
                            Text(text = item.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedIndex) {
            0 -> HomePage()
            1 -> NotificationPage()
            2 -> SettingsPage()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationPreview() {
    BottomNaviigationDemoTheme {
        BottomNavigationApp()
    }
}