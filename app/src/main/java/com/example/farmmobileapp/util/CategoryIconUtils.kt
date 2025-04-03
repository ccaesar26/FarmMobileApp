package com.example.farmmobileapp.util

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.farmmobileapp.R

object CategoryIconUtils {

    @Composable
    fun GetIconForCategory(categoryName: String?) {
        return when (categoryName?.lowercase()) {
            "harvesting" -> Icon(
                painter = painterResource(id = R.drawable.nutrition_24),
                contentDescription = "Harvesting"
            )
            "planting" -> Icon(
                painter = painterResource(id = R.drawable.psychiatry_24),
                contentDescription = "Planting"
            )
            "irrigation" -> Icon(
                painter = painterResource(id = R.drawable.sprinkler_24),
                contentDescription = "Irrigation"
            )
            "fertilization" -> Icon(
                painter = painterResource(id = R.drawable.experiment_24),
                contentDescription = "Fertilization"
            )
            "maintenance" -> Icon(
                painter = painterResource(id = R.drawable.handyman_24),
                contentDescription = "Maintenance"
            )
            "pest and disease control" -> Icon(
                painter = painterResource(id = R.drawable.pest_control_24),
                contentDescription = "Pest and Disease Control"
            )
            else -> Icon(
                painter = painterResource(id = R.drawable.category_24),
                contentDescription = "Default Category Icon"
            )
        }
    }
}