package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.ui.theme.PrimeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel = hiltViewModel(), navController: NavHostController) {
    val state by viewModel.state.collectAsState()

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = state.isLoading,
        onRefresh = {
            viewModel.loadTasks()
        }
    ) {
//        if (state.isLoading) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        } else
        if (state.error != null) {
            Text(text = "Error: ${state.error}")
        } else if (state.tasksByStatus.isNotEmpty()) {
//            TaskStatusSections(tasksByStatus = state.tasksByStatus, navController = navController)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.tasksByStatus.size) { index ->
                    val status = state.tasksByStatus.keys.elementAt(index)
                    val tasks = state.tasksByStatus[status] ?: emptyList()
                    CollapsibleTaskSection(
                        status = status,
                        tasks = tasks,
                        navController = navController
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    Text(
                        text = "Loading tasks...",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(text = "No tasks available.")
                }
            }
        }
    }
}

@Composable
fun TaskStatusSections(
    tasksByStatus: Map<TaskStatus, List<TaskWithField>>,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        tasksByStatus.forEach { (status, tasks) ->
            CollapsibleTaskSection(
                status = status,
                tasks = tasks,
                navController = navController
            )
        }
    }
}

@Composable
fun CollapsibleTaskSection(
    status: TaskStatus,
    tasks: List<TaskWithField>,
    navController: NavHostController
) {
    val isExpanded = remember { mutableStateOf(true) } // State to control section expansion

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300)), // Add smooth animation
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large // Use MaterialTheme shape
    ) {
        Column {
            SectionHeader(
                status = status,
                isExpanded = isExpanded.value,
                onToggleExpand = { isExpanded.value = !isExpanded.value }
            )
            AnimatedVisibility(visible = isExpanded.value) { // Animate visibility of task list
                TaskList(
                    tasks = tasks,
                    navController = navController
                ) // Display TaskList within the collapsible section
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // For SmallTopAppBar in SectionHeader (if you want to use it)
@Composable
fun SectionHeader(status: TaskStatus, isExpanded: Boolean, onToggleExpand: () -> Unit) {
    TopAppBar( // Use SmallTopAppBar for header style
        modifier = Modifier
            .clickable { onToggleExpand() }, // Section header clickable to toggle
        title = {
            Text(
                text = status.name, // Display TaskStatus name as section header
                style = MaterialTheme.typography.titleLarge // Style header text
            )
        },
        actions = {
            IconButton(onClick = onToggleExpand) { // IconButton to toggle expansion
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.padding(end = 8.dp) // Add some padding to the icon
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = when (status) {
                TaskStatus.ToDo ->
                    if (isSystemInDarkTheme()) PrimeColors.Sky.color800
                    else PrimeColors.Sky.color200

                TaskStatus.InProgress ->
                    if (isSystemInDarkTheme()) PrimeColors.Amber.color800
                    else PrimeColors.Amber.color200

                TaskStatus.Completed ->
                    if (isSystemInDarkTheme()) PrimeColors.Green.color800
                    else PrimeColors.Green.color200

                TaskStatus.OnHold ->
                    if (isSystemInDarkTheme()) PrimeColors.Purple.color800
                    else PrimeColors.Purple.color200
            },
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
fun TaskList(
    tasks: List<TaskWithField>,
    navController: NavHostController
) { // Modified TaskList to accept List<TaskWithField>
    Column {
        tasks.forEach { taskWithField ->
            TaskCard(
                taskWithField = taskWithField,
                navController = navController
            ) // Pass TaskWithField to TaskCard
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}