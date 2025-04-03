package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.util.CategoryIconUtils
import com.example.farmmobileapp.util.DateTimeUtils
import java.util.UUID
import androidx.compose.material3.Text as M3Text

@Composable
fun TaskDetailScreen(
    taskWithField: TaskWithField,
    onBack: () -> Unit,
    onUpdateStatus: (TaskStatus) -> Unit, // Callback for updating status
    onMarkAsDone: () -> Unit, // Callback for marking task as done
    navController: NavController
) {
    var showStatusDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Change Status Button
                Button(
                    onClick = { showStatusDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Change Status")
                }

                // Mark as Done Button
                Button(
                    onClick = { onMarkAsDone() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Mark as Done")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Task Title
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = taskWithField.task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                CategoryIconUtils.GetIconForCategory(
                    categoryName = taskWithField.task.categoryName,
                    modifier = Modifier
                        .height(36.dp)
                        .width(36.dp)
                )
            }

            // Task Description
            taskWithField.task.description?.let {
                if (it.isNotEmpty()) Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Task Details
            if (taskWithField.task.dueDate != null) TaskDetailItem(
                label = "Due Date",
                value = DateTimeUtils.formatBackendDateTimeToUserFriendly(taskWithField.task.dueDate),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_clock_24px),
                        contentDescription = "Due Date",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )
            TaskDetailItem(
                label = "Priority",
                value = taskWithField.task.priority.name,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.priority_high_24px),
                        contentDescription = "Priority",
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            )
            TaskDetailItem(
                label = "Status",
                value = taskWithField.task.status.name,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.check_circle_24px),
                        contentDescription = "Status",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )
            TaskDetailItem(
                label = "Recurrence",
                value = taskWithField.task.recurrence.name,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.event_repeat_24px),
                        contentDescription = "Recurrence",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )
            TaskDetailItem(
                label = "Created At",
                value = DateTimeUtils.formatBackendDateTimeToUserFriendly(taskWithField.task.createdAt),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.today_24px),
                        contentDescription = "Created At",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )

            // Assigned Users
//            TaskDetailItem(
//                label = "Assigned Users",
//                value = if (taskWithField.task.assignedUserIds.isNotEmpty())
//                    taskWithField.task.assignedUserIds.joinToString(", ")
//                else "None"
//            )

            // Field Name (if exists)
            taskWithField.field?.let {
                TaskDetailItem(
                    label = "Field",
                    value = it.name,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.map_24px),
                            contentDescription = "Field",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.fillMaxHeight())

            // Status Selection Dialog
            if (showStatusDialog) {
                StatusSelectionDialog(
                    currentStatus = taskWithField.task.status,
                    onDismiss = { showStatusDialog = false },
                    onStatusSelected = {
                        onUpdateStatus(it)
                        showStatusDialog = false
                    }
                )
            }
        }
    }
}

// Reusable Detail Item
@Composable
fun TaskDetailItem(label: String, value: String, leadingIcon: @Composable (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, fontWeight = FontWeight.Bold)
        }
        Text(text = value)
    }
}

// Status Selection Dialog
@Composable
fun StatusSelectionDialog(
    currentStatus: TaskStatus,
    onDismiss: () -> Unit,
    onStatusSelected: (TaskStatus) -> Unit
) {
    val statuses = TaskStatus.entries // Get all TaskStatus values

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Status") },
        text = {
            Column {
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(status) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = status == currentStatus,
                            onClick = { onStatusSelected(status) }
                        )
                        Text(text = status.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
