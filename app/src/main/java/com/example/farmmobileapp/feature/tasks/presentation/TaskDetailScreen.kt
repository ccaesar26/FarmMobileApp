package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority
import java.util.UUID
import androidx.compose.material3.Text as M3Text

@Composable
fun TaskDetailScreen(taskWithField: TaskWithField, onBack: () -> Unit, navController: NavController) { // Add onBack callback
    Surface(
        modifier = Modifier.fillMaxSize(),
        onClick = onBack // Simple click outside to go back for now
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) { // Content within a Box for full-screen
            TaskCard(taskWithField = taskWithField, navController = navController) // Reuse TaskCard to display details
            // You can customize TaskCard further for detail view if needed, or create a separate TaskDetailCard
            // For now, reusing TaskCard for simplicity
        }
    }
}
