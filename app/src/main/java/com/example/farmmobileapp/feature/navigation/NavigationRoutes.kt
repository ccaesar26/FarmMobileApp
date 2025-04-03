package com.example.farmmobileapp.feature.navigation

sealed class NavigationRoutes(val route: String) {
    data object Login : NavigationRoutes("login")
    data object Main : NavigationRoutes("main")
    data object Tasks : NavigationRoutes("tasks")
    data object TaskDetail : NavigationRoutes("taskDetail") { // Route for TaskDetailScreen with taskId argument
        fun createRoute(taskId: String): String {
            return "taskDetail/$taskId"
        }
    }
}