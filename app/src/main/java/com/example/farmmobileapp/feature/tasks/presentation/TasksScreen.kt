package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto

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
fun TaskList(tasks: List<TaskDto>) {
    Column {
        tasks.forEach { task ->
            Text(text = task.title) // Just display title for now
        }
    }
}