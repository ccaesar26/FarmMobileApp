package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.feature.navigation.NavigationRoutes
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority
import com.example.farmmobileapp.util.CategoryIconUtils
import com.example.farmmobileapp.util.DateTimeUtils

@Composable
fun TaskCard(
    taskWithField: TaskWithField,
    navController: NavController
) {
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
            .padding(8.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .clickable {
                navController.navigate("taskDetail"
//                    NavigationRoutes.TaskDetail.createRoute(
//                        task.id
//                    )
                )
            },
        colors = CardDefaults.cardColors(containerColor = priorityColor) // Set card color based on priority
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            task.dueDate?.let { dueDate ->
                val formattedDueDate = DateTimeUtils.formatBackendDateTimeToUserFriendly(dueDate)
                Text(text = "Due Date: $formattedDueDate")
            }
            val formattedCreatedAt =
                DateTimeUtils.formatBackendDateTimeToUserFriendly(task.createdAt)
            Text(text = "Created At: $formattedCreatedAt") // Simple created at display
            Text(text = "Field: ${taskWithField.field?.name ?: "N/A"}") // Display Field Name or N/A if fetch fails
            Row(verticalAlignment = Alignment.CenterVertically) { // Row to align icon and category text
                CategoryIconUtils.GetIconForCategory(task.categoryName)
//                Spacer(modifier = Modifier.padding(start = 8.dp)) // Add some spacing
//                Text(text = "Category: ${task.categoryName ?: "N/A"}") // Category Text
            }
        }
    }
}

@Composable
private fun rememberNavControllerForTaskCard(): NavController { // Helper function for preview NavController
    return rememberNavController()
}