package com.example.farmmobileapp.feature.tasks.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.tasks.data.model.computeCenter
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.tasks.data.model.toMapboxPolygon
import com.example.farmmobileapp.ui.theme.PrimeColors
import com.example.farmmobileapp.util.CategoryIconUtils
import com.example.farmmobileapp.util.DateTimeUtils
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardSatelliteStyle

@Composable
fun TaskDetailScreen(
    taskWithField: TaskWithField,
    onBack: () -> Unit,
    onUpdateStatus: (TaskStatus) -> Unit,
    onMarkAsDone: () -> Unit
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
                    onClick = {
                        onMarkAsDone()
                        onBack()
                    },
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
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if (taskWithField.task.dueDate != null) TaskDetailItem(
                    label = "Due Date",
                    value = DateTimeUtils.formatBackendDateTimeToUserFriendly(taskWithField.task.dueDate),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar_clock_24px),
                            contentDescription = "Due Date",
                            modifier = Modifier.padding(end = 4.dp)
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
                            modifier = Modifier.padding(end = 4.dp)
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
                            modifier = Modifier.padding(end = 4.dp)
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
                            modifier = Modifier.padding(end = 4.dp)
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
                            modifier = Modifier.padding(end = 4.dp)
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
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(256.dp)
                            .padding(top = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                    ) {
                        MapboxMap(
                            modifier = Modifier
                                .fillMaxSize(),
                            mapViewportState = rememberMapViewportState {
                                setCameraOptions {
                                    zoom(13.0)
                                    center(it.boundary.computeCenter())
                                    pitch(0.0)
                                    bearing(0.0)
                                }
                            },
                            // Set Satellite style
                            style = { MapboxStandardSatelliteStyle() }
                        ) {
                            PolygonAnnotation(
                                points = it.boundary.toMapboxPolygon(),
                            ) {
                                fillColor = PrimeColors.Teal.color300
                                fillOutlineColor = PrimeColors.Teal.color500
                                fillOpacity = 0.5

                            }
                        }
                    }
                }
            }
//            Spacer(modifier = Modifier.fillMaxHeight())

            // Status Selection Dialog
            if (showStatusDialog) {
                StatusSelectionDialog(
                    currentStatus = taskWithField.task.status,
                    onDismiss = {
                        showStatusDialog = false
                    },
                    onSaveStatusChanges = {
                        onUpdateStatus(it)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
    onSaveStatusChanges: (TaskStatus) -> Unit
) {
    val statuses = TaskStatus.entries
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Select Status") },
        text = {
            Column {
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStatus = status
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                        )
                        Text(text = status.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        },
        confirmButton = {
            Button(onClick = {
                onSaveStatusChanges(selectedStatus)
                onDismiss()
            }) {
                Text("Save")
            }
        }
    )
}
