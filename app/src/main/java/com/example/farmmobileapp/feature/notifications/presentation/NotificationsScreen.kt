package com.example.farmmobileapp.feature.notifications.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle // For Mark as Read
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmmobileapp.feature.notifications.data.model.NotificationDisplay
import com.example.farmmobileapp.feature.notifications.data.model.NotificationDto
// Import your date formatter
import com.example.farmmobileapp.util.extensions.formatIsoInstantToLocal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") })
            // Optional: Add a "Mark all as read" button here
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.notificationDisplays.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.notificationDisplays.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else if (uiState.notificationDisplays.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications yet.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notificationDisplays, key = { it.id }) { notification ->
                        NotificationItem(
                            notification = notification,
                            onMarkAsRead = { viewModel.markNotificationAsRead(notification) },
                            onClick = {
                                // TODO: Implement navigation based on notification.type and notification.entityId
                                // e.g., if (notification.type == "NewTask") navController.navigate("taskDetail/${notification.entityId}")
//                                Log.d("NotificationClick", "Clicked notification: ${notification.id}, type: ${notification.type}, entity: ${notification.entityId}")
                            }
                        )
                    }
                }
            }

            // Display SignalR connection status (optional)
            if (uiState.realtimeError != null) {
                Text(
                    text = "Real-time Error: ${uiState.realtimeError}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (uiState.isRealtimeConnected) {
                Text(
                    text = "Real-time updates connected",
                    color = Color.Green.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: NotificationDisplay,
    onMarkAsRead: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on type (example)
            Icon(
                imageVector = if (notification.isRead) Icons.Default.Notifications else Icons.Default.NotificationsActive,
                contentDescription = "Notification icon",
                tint = if (notification.isRead) Color.Gray else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.notificationType.formatNotificationType(), // Helper to format title
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                )
                // You'd ideally have a more descriptive message based on type/entity
//                Text(
//                    text = "Related to: ${notification.sourceEntityId}", // Placeholder message
//                    style = MaterialTheme.typography.bodyMedium,
//                    maxLines = 2
//                )
                Text(
                    text = notification.timestamp.formatIsoInstantToLocal() ?: "Unknown time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            if (!notification.isRead) {
                IconButton(onClick = onMarkAsRead) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Mark as read", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Helper to format notification type for display (example)
fun String.formatNotificationType(): String {
    return when (this) {
        "NewTask" -> "New Task Assigned"
        "NewTaskNotification" -> "New Task Assigned" // Match what backend sends
        "NewReport" -> "New Report Created"
        "NewReportComment" -> "New Comment on Report"
        else -> replace("Notification", "").replace(Regex("(?<=[a-z])(?=[A-Z])"), " ") // Basic formatting
    }
}