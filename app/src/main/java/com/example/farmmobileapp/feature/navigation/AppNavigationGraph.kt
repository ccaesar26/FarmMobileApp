package com.example.farmmobileapp.feature.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.farmmobileapp.feature.auth.presentation.LoginScreen
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.feature.tasks.data.model.enums.RecurrenceType
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.tasks.presentation.TaskDetailScreen
import com.example.farmmobileapp.feature.tasks.presentation.TaskWithField
import com.example.farmmobileapp.feature.tasks.presentation.TasksScreen
import com.example.farmmobileapp.main.MainScreen
import com.example.farmmobileapp.main.MainViewModel

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isAuthenticated by mainViewModel.isAuthenticated.collectAsState()
    val startDestinationRoute =
        if (isAuthenticated) NavigationRoutes.Main.route else NavigationRoutes.Login.route

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
                popUpTo(NavigationRoutes.Login.route) {
                    inclusive = true
                } // Prevent going back to login
            }
        } else if (!isAuthenticated && navController.currentDestination?.route != NavigationRoutes.Login.route) {
            navController.navigate(NavigationRoutes.Login.route) {
                popUpTo(NavigationRoutes.Main.route) {
                    inclusive = true
                } // Prevent going back to main if not authenticated
            }
        }
    }
}

object TasksScreenDefaults { // Temporary object for Preview and dummy data
    // Dummy TaskDto for preview
    val dummyTask = TaskDto(
        id = "",
        title = "Sample Task Title",
        description = "Sample task description - this will be shown in detail view",
        dueDate = "2024-07-20T10:00:00",
        priority = TaskPriority.Medium,
        status = TaskStatus.ToDo,
        assignedUserIds = emptyList(),
        categoryId = "UUID.randomUUID()",
        categoryName = "Harvesting",
        recurrence = RecurrenceType.None,
        recurrenceEndDate = null,
        fieldId = "UUID.randomUUID()",
        commentsCount = 0,
        createdAt = "2024-07-15T14:30:00"
    )
    val dummyTaskWithField = TaskWithField(dummyTask, null)
}