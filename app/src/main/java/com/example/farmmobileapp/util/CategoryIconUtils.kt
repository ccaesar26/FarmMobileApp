package com.example.farmmobileapp.util

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.farmmobileapp.R

object CategoryIconUtils {

    @Composable
    fun GetIconForCategory(categoryName: String?, modifier: Modifier = Modifier) {
        return when (categoryName?.lowercase()) {
            "harvesting" -> Icon(
                painter = painterResource(id = R.drawable.agriculture_24),
                contentDescription = "Harvesting",
                modifier = modifier
            )
            "planting" -> Icon(
                painter = painterResource(id = R.drawable.psychiatry_24),
                contentDescription = "Planting",
                modifier = modifier
            )
            "irrigation" -> Icon(
                painter = painterResource(id = R.drawable.sprinkler_24),
                contentDescription = "Irrigation",
                modifier = modifier
            )
            "fertilization" -> Icon(
                painter = painterResource(id = R.drawable.experiment_24),
                contentDescription = "Fertilization",
                modifier = modifier
            )
            "maintenance" -> Icon(
                painter = painterResource(id = R.drawable.handyman_24),
                contentDescription = "Maintenance",
                modifier = modifier
            )
            "pest and disease control" -> Icon(
                painter = painterResource(id = R.drawable.pest_control_24),
                contentDescription = "Pest and Disease Control",
                modifier = modifier
            )
            else -> Icon(
                painter = painterResource(id = R.drawable.category_24),
                contentDescription = "Default Category Icon",
                modifier = modifier
            )
        }
    }
}