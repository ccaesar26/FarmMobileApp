package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority

@Composable
fun TasksScreen(viewModel: TasksViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(text = "Error: ${state.error}")
        } else if (state.tasks.isNotEmpty()) {
            TaskList(tasks = state.tasks)
        } else {
            Text(text = "No tasks available.")
        }
    }
}

@Composable
fun TaskCard(taskWithField: TaskWithField) {
    val task = taskWithField.task
    val priorityColor = when (task.priority) {
        TaskPriority.Low -> Color.Green.copy(alpha = 0.7f)
        TaskPriority.Medium -> Color.Yellow.copy(alpha = 0.7f)
        TaskPriority.High -> Color.Red.copy(alpha = 0.8f)
        TaskPriority.Urgent -> Color.Red.copy(alpha = 0.9f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = priorityColor) // Set card color based on priority
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            task.dueDate?.let { dueDate ->
                Text(text = "Due Date: $dueDate") // Simple date display for now
            }
            Text(text = "Created At: ${task.createdAt}") // Simple created at display
            Text(text = "Field: ${taskWithField.field?.name ?: "N/A"}") // Display Field Name or N/A if fetch fails
            Text(text = "Category: ${task.categoryName ?: "N/A"}") // Display category name or N/A
        }
    }
}

@Composable
fun TaskList(tasks: List<TaskWithField>) {
    Column {
        tasks.forEach { task ->
            TaskCard(taskWithField = task)
        }
    }
}