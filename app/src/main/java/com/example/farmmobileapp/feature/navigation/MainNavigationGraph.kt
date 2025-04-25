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
import com.example.farmmobileapp.feature.reports.presentation.ReportsScreen
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
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

                taskWithField?.let { taskWithField ->
                    TaskDetailScreen(
                        taskWithField = taskWithField,
                        onBack = { navController.popBackStack() },
                        onUpdateStatus = { newStatus ->
                            tasksViewModel.updateTaskStatus(taskWithField.task.id, newStatus)
                            // navigate to the same page with updated task and pop the back stack
                            navController.navigate(NavigationRoutes.TaskDetail.createRoute(taskWithField.task.id)) {
                                popUpTo(NavigationRoutes.TaskDetail.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onMarkAsDone = {
                            tasksViewModel.updateTaskStatus(
                                taskWithField.task.id,
                                TaskStatus.Completed
                            )
                        }
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
