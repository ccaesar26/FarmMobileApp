package com.example.farmmobileapp.feature.navigation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.farmmobileapp.feature.notifications.presentation.NotificationsScreen
import com.example.farmmobileapp.feature.reports.presentation.CreateReportScreen
import com.example.farmmobileapp.feature.reports.presentation.ReportDetailScreen
import com.example.farmmobileapp.feature.reports.presentation.ReportDetailViewModel
import com.example.farmmobileapp.feature.reports.presentation.ReportListScreen
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.tasks.presentation.TaskDetailScreen
import com.example.farmmobileapp.feature.tasks.presentation.TaskWithField
import com.example.farmmobileapp.feature.tasks.presentation.TasksScreen
import com.example.farmmobileapp.feature.tasks.presentation.TasksViewModel
import com.example.farmmobileapp.feature.users.presentation.ProfileScreen
import com.example.farmmobileapp.main.MainViewModel

@Composable
fun MainNavigationGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
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
                    tasksViewModel.loadComments(taskId)
                }

                taskWithField?.let { taskWithField ->
                    TaskDetailScreen(
                        taskWithField = taskWithField,
                        taskState = tasksViewModel.state,
                        onBack = { navController.popBackStack() },
                        onUpdateStatus = { newStatus ->
                            tasksViewModel.updateTaskStatus(taskWithField.task.id, newStatus)
                            // navigate to the same page with updated task and pop the back stack
                            navController.navigate(
                                NavigationRoutes.TaskDetail.createRoute(
                                    taskWithField.task.id
                                )
                            ) {
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
                        },
                        onAddComment = { comment ->
                            tasksViewModel.addComment(taskWithField.task.id, comment)
                        },
                    )
                } ?: CircularProgressIndicator()
            }
        }
        navigation(
            startDestination = NavigationRoutes.ReportsList.route,
            route = NavigationRoutes.Reports.route
        ) {
            composable(route = NavigationRoutes.ReportsList.route) {
                ReportListScreen(navController = navController)
            }
            composable(
                route = NavigationRoutes.ReportDetail.route,
                arguments = listOf(navArgument("reportId") { type = NavType.StringType })
            ) { backStackEntry ->
                val reportViewModel = hiltViewModel<ReportDetailViewModel>()

                val reportId = backStackEntry.arguments?.getString("reportId") ?: return@composable
                reportViewModel.reportId = reportId // Set the reportId in ViewModel

                ReportDetailScreen(navController = navController)
            }
            composable(route = NavigationRoutes.ReportCreate.route) {
                CreateReportScreen(navController = navController)
            }
        }
        composable(NavigationRoutes.Profile.route) {
            ProfileScreen(mainViewModel = mainViewModel)
        }
        composable(NavigationRoutes.Notifications.route) {
            NotificationsScreen(navController = navController)
        }
    }
}
