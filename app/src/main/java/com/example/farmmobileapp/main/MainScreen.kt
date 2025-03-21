package com.example.farmmobileapp.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.components.bottomBar.BottomNavItems
import com.example.farmmobileapp.components.bottomBar.FarmBottomBar
import com.example.farmmobileapp.components.topBar.FarmTopBar
import com.example.farmmobileapp.feature.users.presentation.ProfileScreen
import com.example.farmmobileapp.components.topBar.TopBarActions
import com.example.farmmobileapp.feature.reports.presentation.ReportsScreen
import com.example.farmmobileapp.feature.tasks.presentation.TasksScreen

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            FarmTopBar(title = "FarmForge", actions = TopBarActions.actions)
        },
        bottomBar = {
            FarmBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply padding from Scaffold
            contentAlignment = Alignment.Center
        ) {
            MainScreenContent(navController = navController) // Content based on bottom navigation
        }
    }
}

@Composable
fun MainScreenContent(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItems.items.first().route) {
        composable(BottomNavItems.items[0].route) {
            TasksScreen() // Placeholder for Tasks Screen
        }
        composable(BottomNavItems.items[1].route) {
            ReportsScreen() // Placeholder for Report Screen
        }
        composable(BottomNavItems.items[2].route) {
            ProfileScreen() // Placeholder for Profile Screen
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
