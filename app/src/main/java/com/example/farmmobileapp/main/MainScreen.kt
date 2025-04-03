package com.example.farmmobileapp.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.components.bottomBar.FarmBottomBar
import com.example.farmmobileapp.components.topBar.FarmTopBar
import com.example.farmmobileapp.components.topBar.TopBarActions
import com.example.farmmobileapp.feature.navigation.AppNavigationGraph
import com.example.farmmobileapp.feature.navigation.MainNavigationGraph
import com.example.farmmobileapp.ui.theme.PrimeColors

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            FarmTopBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Farm",
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Insight",
                            fontWeight = FontWeight.Bold,
                            color = PrimeColors.Teal.color500
                        )
                    }
                },
                actions = TopBarActions.actions
            )
        },
        bottomBar = {
            FarmBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply padding from Scaffold
            contentAlignment = Alignment.Center
        ) {
            MainNavigationGraph(navController = navController) // Navigation graph for bottom navigation
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
