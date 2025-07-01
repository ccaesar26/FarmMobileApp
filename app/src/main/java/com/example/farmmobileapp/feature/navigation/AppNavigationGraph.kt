package com.example.farmmobileapp.feature.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.feature.auth.presentation.LoginScreen
import com.example.farmmobileapp.main.MainScreen
import com.example.farmmobileapp.main.MainViewModel
import com.example.farmmobileapp.main.NavigationCommand
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState()

    val startDestinationRoute = remember(isAuthenticated) {
        if (isAuthenticated) NavigationRoutes.Main.route else NavigationRoutes.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationRoute
    ) {
        composable(route = NavigationRoutes.Login.route) {
            LoginScreen()
        }
        composable(route = NavigationRoutes.Main.route) {
            MainScreen(mainViewModel = mainViewModel)
        }
    }

    // --- Listen for Navigation Commands from MainViewModel ---
    LaunchedEffect(Unit) { // Launch once
        mainViewModel.navigationCommand
            .onEach { command ->
                when (command) {
                    NavigationCommand.NavigateToLogin -> {
                        // Perform navigation using the TOP-LEVEL navController
                        navController.navigate(NavigationRoutes.Login.route) {
                            popUpTo(NavigationRoutes.Main.route) { // Pop Main screen off
                                inclusive = true
                            }
                            // Optionally add launchSingleTop = true
                        }
                    }
                    // Handle other potential navigation commands here
                }
            }
            .launchIn(this) // Use scope from LaunchedEffect
    }

    // --- Authentication State Change Navigation (Keep this, but simplify popUpTo) ---
    // This handles cases where auth state changes while the user is on a screen
    LaunchedEffect(isAuthenticated, navController) { // Add navController as key
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (isAuthenticated && currentRoute != NavigationRoutes.Main.route) {
            // Navigate to Main, ensuring Login is removed from backstack
            navController.navigate(NavigationRoutes.Main.route) {
                popUpTo(navController.graph.findStartDestination().id) { // Pop up to the start of THIS graph
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else if (!isAuthenticated && currentRoute != NavigationRoutes.Login.route) {
            // Navigate to Login, ensuring Main is removed from backstack
            navController.navigate(NavigationRoutes.Login.route) {
                popUpTo(navController.graph.findStartDestination().id) { // Pop up to the start of THIS graph
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}