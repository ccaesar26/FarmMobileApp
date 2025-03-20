package com.example.farmmobileapp.ui.navigation

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.farmmobileapp.R

// Data class for Bottom Navigation items
data class BottomNavItem(
    val name: String,
    val icon: ImageVector,
    val route: String // Route for Navigation Compose
)

// Data class for Top Bar Actions (Icons)
data class TopBarAction(
    val icon: ImageVector,
    val onClick: () -> Unit // Action to perform when icon is clicked
)

object NavigationItems {
    // Define your Bottom Navigation Items
    val BottomNavItems = listOf(
        BottomNavItem(
            name = "Tasks",
            icon = Icons.Rounded.ViewDay,
            route = "tasks"
        ),
        BottomNavItem(
            name = "Report",
            icon = Icons.Rounded.Campaign,
            route = "report"
        ),
        BottomNavItem(
            name = "Profile",
            icon = Icons.Rounded.Person,
            route = "profile"
        )
    )

    // Define your Top Bar Actions
    val TopBarActions = listOf(
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