package com.example.farmmobileapp.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import com.example.farmmobileapp.components.bottomBar.FarmBottomBar
import com.example.farmmobileapp.components.topBar.FarmTopBar
import com.example.farmmobileapp.components.topBar.TopBarAction
import com.example.farmmobileapp.components.topBar.TopBarActions
import com.example.farmmobileapp.feature.navigation.MainNavigationGraph
import com.example.farmmobileapp.feature.navigation.NavigationRoutes
import com.example.farmmobileapp.ui.theme.PrimeColors
import com.mapbox.common.MapboxOptions
import com.example.farmmobileapp.R

@Composable
fun MainScreen(
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()
    val mapboxTokenState = mainViewModel.mapboxToken.collectAsState()
    val mapboxToken = mapboxTokenState.value

    // Initialize MapboxOptions.accessToken when token is available
    LaunchedEffect(mapboxToken) {
        if (mapboxToken != null) {
            MapboxOptions.accessToken = mapboxToken // Set the access token
            println("Mapbox Access Token set: ${MapboxOptions.accessToken}")
            // **Now you can proceed to initialize and use your Mapbox MapView/MapboxMap**
            // (e.g., create MapView, get MapboxMap, etc.)
        } else {
            println("Mapbox Token is not available yet or failed to load.")
            // Handle the case where the token is not available
            // (e.g., show a loading indicator, error message, or retry mechanism)
        }
    }

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
                actions = listOf(
                    TopBarAction(
                    icon = ImageVector.vectorResource(id = R.drawable.rounded_notifications_24),
                    onClick = {
                        navController.navigate(NavigationRoutes.Notifications.route)
                    }
                ))
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
            MainNavigationGraph(
                navController = navController,
                mainViewModel = mainViewModel
            ) // Navigation graph for bottom navigation
        }
    }
}
