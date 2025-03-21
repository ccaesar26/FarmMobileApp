package com.example.farmmobileapp.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.feature.auth.presentation.LoginScreen
import com.example.farmmobileapp.main.MainScreen
import com.example.farmmobileapp.main.MainViewModel

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState()
    val startDestinationRoute = if (isAuthenticated) NavigationRoutes.Main.route else NavigationRoutes.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestinationRoute
    ) {
        composable(route = NavigationRoutes.Login.route) {
            LoginScreen()
        }
        composable(route = NavigationRoutes.Main.route) {
            MainScreen()
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated && navController.currentDestination?.route != NavigationRoutes.Main.route) {
            navController.navigate(NavigationRoutes.Main.route) {
                popUpTo(NavigationRoutes.Login.route) { inclusive = true } // Prevent going back to login
            }
        } else if (!isAuthenticated && navController.currentDestination?.route != NavigationRoutes.Login.route) {
            navController.navigate(NavigationRoutes.Login.route) {
                popUpTo(NavigationRoutes.Main.route) { inclusive = true } // Prevent going back to main if not authenticated
            }
        }
    }
}