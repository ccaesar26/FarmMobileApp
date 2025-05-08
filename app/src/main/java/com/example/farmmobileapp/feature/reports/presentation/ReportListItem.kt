package com.example.farmmobileapp.feature.reports.presentation

// ... other imports ...
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // Assuming you need Card, Text etc.
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Import Color
import androidx.compose.ui.text.font.FontWeight // Import FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.farmmobileapp.feature.navigation.NavigationRoutes
import com.example.farmmobileapp.feature.reports.data.model.ReportWithField
import com.example.farmmobileapp.feature.reports.data.model.enums.ReportStatus // Import your Enum
// Import your date formatting extension function
import com.example.farmmobileapp.util.extensions.formatIsoInstantToLocal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListItem(report: ReportWithField, navController: NavController) {
    Card(
        onClick = {
            navController.navigate(NavigationRoutes.ReportDetail.createRoute(report.report.id))
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Add consistent spacing
        ) {
            // --- Title ---
            Text(
                text = report.report.title,
                style = MaterialTheme.typography.titleMedium, // Slightly smaller than titleLarge perhaps
                fontWeight = FontWeight.SemiBold // Make title stand out
            )

            // --- Description ---
            if (!report.report.description.isNullOrBlank()) { // Show only if description exists
                Text(
                    text = report.report.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2 // Keep max lines
                )
                Spacer(Modifier.height(4.dp)) // Add extra space after description if present
            }

            // --- Field Name ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Field: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    // Use the fieldName from ReportWithField
                    text = report.fieldName, // Already handles "Unknown Field" etc. from VM
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // --- Status ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Status: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                // Display Status with potential color coding
                Text(
                    text = report.report.status.displayName(), // Use a helper function for display name
                    style = MaterialTheme.typography.bodySmall,
                    color = report.report.status.displayColor() // Use a helper function for color
                )
            }

            // --- Creation Date ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Created: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = report.report.createdAt.formatIsoInstantToLocal().toString(), // Use formatter
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Optional: Comment Count (if you added it to ReportWithField)
            // if (report.commentCount > 0) {
            //      Row(verticalAlignment = Alignment.CenterVertically) {
            //          Text(
            //              text = "Comments: ",
            //              style = MaterialTheme.typography.bodySmall,
            //              fontWeight = FontWeight.Medium
            //          )
            //          Text(
            //              text = report.commentCount.toString(),
            //              style = MaterialTheme.typography.bodySmall
            //          )
            //      }
            // }

        } // End Column
    } // End Card
}

// --- Helper Extension Functions for ReportStatus ---
// (Place these in a relevant file, e.g., within the enums package or a UI utils file)

// Example: Get a user-friendly display name for the enum
fun ReportStatus.displayName(): String {
    return when (this) {
        ReportStatus.Submitted -> "Submitted"
        ReportStatus.Seen -> "Seen"
        ReportStatus.InProgress -> "In Progress"
        ReportStatus.Resolved -> "Resolved"
        ReportStatus.Closed -> "Closed"
        // Add other statuses as needed
        // else -> this.name // Fallback to enum name
    }
}

// Example: Get a color based on the status
@Composable
fun ReportStatus.displayColor(): Color {
    return when (this) {
        ReportStatus.Submitted -> MaterialTheme.colorScheme.onSurfaceVariant // Or a specific blue/gray
        ReportStatus.Seen -> Color(0xFF0000FF) // Blue
        ReportStatus.InProgress -> Color(0xFFFFA500) // Orange
        ReportStatus.Resolved -> Color(0xFF008000) // Green
        ReportStatus.Closed -> MaterialTheme.colorScheme.outline // Gray
        // Add other statuses as needed
        // else -> LocalContentColor.current
    }
}

// --- Ensure ReportStatus enum is defined ---
// package com.example.farmmobileapp.feature.reports.data.model.enums
// enum class ReportStatus { Submitted, InProgress, Resolved, Closed /*, ... */ }