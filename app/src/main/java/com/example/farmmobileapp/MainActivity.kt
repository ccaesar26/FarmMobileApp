package com.example.farmmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.ui.screens.LoginScreen
import com.example.farmmobileapp.ui.screens.MainScreen
import com.example.farmmobileapp.ui.theme.AppTheme
import com.example.farmmobileapp.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Main : Screen("main")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState()
    val startDestinationRoute = if (isAuthenticated) Screen.Main.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestinationRoute
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen()
        }
        composable(route = Screen.Main.route) {
            MainScreen()
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated && navController.currentDestination?.route != Screen.Main.route) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true } // Prevent going back to login
            }
        } else if (!isAuthenticated && navController.currentDestination?.route != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Main.route) { inclusive = true } // Prevent going back to main if not authenticated
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppTheme {
        AppNavigation() // Preview the navigation setup
    }
}