package com.example.farmmobileapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.ui.navigation.NavigationItems
import com.example.farmmobileapp.ui.navigation.TopBarAction
import com.example.farmmobileapp.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            FarmForgeTopBar(title = "FarmForge", actions = NavigationItems.TopBarActions)
        },
        bottomBar = {
            FarmForgeBottomBar(navController = navController)
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
    NavHost(navController = navController, startDestination = NavigationItems.BottomNavItems.first().route) {
        composable(NavigationItems.BottomNavItems[0].route) {
            TasksScreen() // Placeholder for Tasks Screen
        }
        composable(NavigationItems.BottomNavItems[1].route) {
            ReportScreen() // Placeholder for Report Screen
        }
        composable(NavigationItems.BottomNavItems[2].route) {
            ProfileScreen() // Placeholder for Profile Screen
        }
    }
}

@Composable
fun TasksScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Tasks Screen")
    }
}

@Composable
fun ReportScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Report Screen")
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Profile Screen")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmForgeTopBar(title: String, actions: List<TopBarAction>) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            actions.forEach { action ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    IconButton(onClick = action.onClick) {
                        Icon(imageVector = action.icon, contentDescription = action.icon.name)
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@Composable
fun FarmForgeBottomBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        NavigationItems.BottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(item.name) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
