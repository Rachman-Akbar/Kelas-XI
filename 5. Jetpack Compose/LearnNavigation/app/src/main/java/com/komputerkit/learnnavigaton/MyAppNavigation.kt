package com.komputerkit.learnnavigaton

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun MyAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SCREEN_A
    ) {
        // Composable untuk Screen A
        composable(route = Routes.SCREEN_A) {
            ScreenA(
                onNavigateToScreenB = { name ->
                    // Encode nama untuk menghindari masalah dengan karakter khusus
                    val encodedName = java.net.URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.createScreenBRoute(encodedName))
                }
            )
        }
        
        // Composable untuk Screen B dengan parameter nama
        composable(route = Routes.SCREEN_B) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("name")
            val decodedName = encodedName?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            
            ScreenB(
                receivedName = decodedName,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}