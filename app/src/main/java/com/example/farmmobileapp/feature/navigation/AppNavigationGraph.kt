package com.example.farmmobileapp.feature.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
        composable(route = NavigationRoutes.Tasks.route) {
            TasksScreen(navController = navController) // Pass navController to TasksScreen
        }
        composable(
            route = NavigationRoutes.TaskDetail.route,
//            arguments = listOf(navArgument(name = "taskId") {
//                type = NavType.StringType
//            }) // Define taskId argument
        ) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getString("taskId") // Extract taskId from route
            // TODO: Fetch TaskWithField based on taskId (if needed) or pass it directly via navigation if available
            // For now, just pass a dummy TaskWithField for demonstration
            val dummyTaskWithField =
                TasksScreenDefaults.dummyTaskWithField // Using dummy from TasksScreenDefaults for now
            if (dummyTaskWithField != null) {
                TaskDetailScreen(
                    taskWithField = dummyTaskWithField,
                    onBack = { navController.navigateUp() },
                    navController = navController
                ) // Pass onBack to navigateUp
            } else {
                Text("Error: Task details not available") // Handle case where dummyTaskWithField is null
            }
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