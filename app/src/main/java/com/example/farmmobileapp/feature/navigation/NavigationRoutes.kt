package com.example.farmmobileapp.feature.navigation

sealed class NavigationRoutes(val route: String) {
    data object Login : NavigationRoutes("login")
    data object Main : NavigationRoutes("main")
    data object Tasks : NavigationRoutes("tasks")
}