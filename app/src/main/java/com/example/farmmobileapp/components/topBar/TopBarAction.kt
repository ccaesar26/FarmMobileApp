package com.example.farmmobileapp.components.topBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector


// Data class for Top Bar Actions (Icons)
data class TopBarAction(
    val icon: ImageVector,
    val onClick: () -> Unit
)

object TopBarActions {
    val actions = listOf(
        TopBarAction(
            icon = Icons.Rounded.Notifications,
            onClick = { /* TODO: Implement Notification action */ }
        ),
        TopBarAction(
            icon = Icons.Rounded.Settings,
            onClick = { /* TODO: Implement Settings action */ }
        )
    )
}