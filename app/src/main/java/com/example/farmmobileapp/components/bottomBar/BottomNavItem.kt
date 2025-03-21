package com.example.farmmobileapp.components.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val icon: ImageVector,
    val route: String
)

object BottomNavItems {
    val items = listOf(
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
}
