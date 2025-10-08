package com.komputerkit.newsnow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.komputerkit.newsnow.navigation.Routes
import com.komputerkit.newsnow.ui.screen.HomePage as HomePageScreen
import com.komputerkit.newsnow.ui.screen.NewsArticlePage as NewsArticlePageScreen
import com.komputerkit.newsnow.ui.theme.NewsNowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsNowTheme {
                NewsApp()
            }
        }
    }
}

@Composable
fun NewsApp() {
    val navController = rememberNavController()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME_PAGE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Routes.HOME_PAGE) {
                HomePageScreen(navController = navController)
            }
            
            composable(
                route = Routes.newsArticlePageWithUrl("{url}"),
                arguments = listOf(
                    navArgument("url") { 
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                NewsArticlePageScreen(
                    navController = navController,
                    url = url
                )
            }
        }
    }
}