package com.example.farmmobileapp.feature.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.farmmobileapp.components.bottomBar.BottomNavItems
import com.example.farmmobileapp.feature.reports.presentation.ReportsScreen
import com.example.farmmobileapp.feature.tasks.presentation.TaskDetailScreen
import com.example.farmmobileapp.feature.tasks.presentation.TaskWithField
import com.example.farmmobileapp.feature.tasks.presentation.TasksScreen
import com.example.farmmobileapp.feature.tasks.presentation.TasksViewModel
import com.example.farmmobileapp.feature.users.presentation.ProfileScreen

@Composable
fun MainNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Tasks.route
    ) {
        navigation(
            startDestination = NavigationRoutes.TaskList.route,
            route = NavigationRoutes.Tasks.route
        ) {
            composable(route = NavigationRoutes.TaskList.route) {
                val tasksViewModel = hiltViewModel<TasksViewModel>()
                TasksScreen(viewModel = tasksViewModel, navController = navController)
            }
            composable(
                route = NavigationRoutes.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tasksViewModel = hiltViewModel<TasksViewModel>()
                val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
                var taskWithField by remember { mutableStateOf<TaskWithField?>(null) }

                LaunchedEffect(taskId) {
                    taskWithField = tasksViewModel.getTaskById(taskId)
                }

                taskWithField?.let { task ->
                    TaskDetailScreen(
                        taskWithField = task,
                        onBack = { navController.popBackStack() },  // Navigate back to TasksScreen
                        onUpdateStatus = { status ->
                            // Handle status update
                        },
                        onMarkAsDone = {
                            // Handle mark as done
                        },
                        navController = navController
                    )
                } ?: CircularProgressIndicator()
            }
        }
        composable(NavigationRoutes.Reports.route) {
            ReportsScreen() // Placeholder for Report Screen
        }
        composable(NavigationRoutes.Profile.route) {
            ProfileScreen() // Placeholder for Profile Screen
        }
    }
}
