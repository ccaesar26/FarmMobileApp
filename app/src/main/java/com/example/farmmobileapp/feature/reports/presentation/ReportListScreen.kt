package com.example.farmmobileapp.feature.reports.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmmobileapp.feature.navigation.NavigationRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(
    navController: NavController,
    viewModel: ReportListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Use Box as the main layout container
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = {
            viewModel.loadReportsAndFields(forceRefresh = true)
        }
    ) {
        // --- Main Content Area ---
//        if (uiState.isLoading) {
        // Explicitly align the indicator in the center of the Box
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//        } else
        if (uiState.reportsWithFields.isEmpty() && uiState.error == null) {
            // Explicitly align the empty text in the center of the Box
            Text("No reports yet.", modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            // Handle error display if needed (e.g., centered text)
            Text(
                text = "Error: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            // LazyColumn takes up the available space defined by the Box
            LazyColumn(
                modifier = Modifier.fillMaxSize(), // Fill the Box
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp // Add extra bottom padding so FAB doesn't overlap last item significantly
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.reportsWithFields.size) { index ->
                    ReportListItem(
                        report = uiState.reportsWithFields[index],
                        navController = navController
                    )
                    // Add HorizontalDivider if needed between items
                    // HorizontalDivider()
                }
            }
        } // --- End of Main Content Area ---


        // --- Floating Action Button ---
        // Place the FAB as another child inside the Box
        FloatingActionButton(
            onClick = {
                navController.navigate(NavigationRoutes.ReportCreate.route)
            },
            // Align this specific Composable within the Box
            modifier = Modifier
                .align(Alignment.BottomEnd) // Position to bottom-right
                .padding(16.dp) // Add padding from the screen edges
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create Report")
        }
    } // --- End of Box ---
}

