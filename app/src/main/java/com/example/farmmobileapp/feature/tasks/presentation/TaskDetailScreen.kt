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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.fields.data.model.computeCenter
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.fields.data.model.toMapboxPolygon
import com.example.farmmobileapp.ui.theme.PrimeColors
import com.example.farmmobileapp.util.CategoryIconUtils
import com.example.farmmobileapp.util.DateTimeUtils
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardSatelliteStyle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun TaskDetailScreen(
    taskWithField: TaskWithField,
    taskState: StateFlow<TasksState>,
    onBack: () -> Unit,
    onUpdateStatus: (TaskStatus) -> Unit,
    onMarkAsDone: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }
    val taskState by taskState.collectAsState()

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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
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
                                    zoom(12.0)
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

            // Comments Section (New)
            TaskCommentsSection(
                comments = taskState.comments,
                isLoading = taskState.isCommentsLoading,
                error = taskState.commentsError,
                isAddingComment = taskState.isAddingComment,
                onAddComment = onAddComment
            )

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

@Composable
fun TaskCommentsSection(
    comments: List<TaskCommentWithUser>,
    isLoading: Boolean,
    error: String?,
    isAddingComment: Boolean,
    onAddComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp, start = 16.dp, end = 16.dp)) { // Added padding
        Text("Comments", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Error loading comments: $error", color = MaterialTheme.colorScheme.error)
        } else if (comments.isEmpty()) {
            Text("No comments yet. Be the first to add one!", style = MaterialTheme.typography.bodySmall)
        } else {
            // Use LazyColumn if you expect many comments, otherwise a simple Column is fine
            // For a chat-like feel, LazyColumn is better.
            // Max height to prevent comments from taking over the screen before scrolling
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp) // Restrict height, make it scrollable independently
            ) {
                items(items = comments.sortedBy { it.taskComment.getCreatedAtAsMillis() }) { comment ->
                    CommentItem(comment = comment)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input field for new comment
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Add a comment...") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (commentText.text.isNotBlank()) {
                        onAddComment(commentText.text)
                        commentText = TextFieldValue("") // Clear field after sending
                    }
                },
                enabled = commentText.text.isNotBlank() && !isAddingComment
            ) {
                if (isAddingComment) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send comment")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Space at the very bottom
    }
}

@Composable
fun CommentItem(comment: TaskCommentWithUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.userName, // Display userName
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.getUserFriendlyCreatedAt(), // You might need a shorter format
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = comment.taskComment.comment, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
