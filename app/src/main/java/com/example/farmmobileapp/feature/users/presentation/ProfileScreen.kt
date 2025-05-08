package com.example.farmmobileapp.feature.users.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout // Icon for logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.farmmobileapp.feature.navigation.NavigationRoutes // Import your routes
import com.example.farmmobileapp.feature.users.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController, // To navigate away on logout
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- Side Effects ---
    // Handle general loading errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearError() // Clear after showing
        }
    }

    // Handle logout errors
    LaunchedEffect(uiState.logoutError) {
        uiState.logoutError?.let {
            snackbarHostState.showSnackbar("Logout Error: $it")
            viewModel.resetLogoutStatus() // Clear error after showing
        }
    }

    // Handle logout success: Navigate to Login
    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            // Navigate to Login screen and clear the back stack
            navController.navigate(NavigationRoutes.Login.route) { // Use your actual Login route
//                popUpTo(NavigationRoutes.Login.route) { // Pop up to the start of the graph
//                    inclusive = true // Include the start destination itself to clear everything
//                }
//                launchSingleTop = true // Avoid multiple copies of Login screen
            }
            viewModel.resetLogoutStatus() // Reset flag
        }
    }

    // --- UI ---
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // You can customize this - maybe show username or just "Profile"
            TopAppBar(title = { Text(uiState.userProfile?.name ?: "Profile") })
            // Add navigation icon if needed (e.g., back button if this isn't a root screen)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Make content scrollable if needed
                .padding(16.dp), // Padding for content
            horizontalAlignment = Alignment.CenterHorizontally // Center items like button
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.userProfile != null) {
                // Display Profile Details
                UserProfileDetails(profile = uiState.userProfile!!)
                Spacer(Modifier.height(32.dp)) // Space before logout button

                // Logout Button
                Button(
                    onClick = { viewModel.logout() },
                    enabled = !uiState.isLoggingOut, // Disable while logging out
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // Use error color for logout
                ) {
                    if (uiState.isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onError // Ensure indicator is visible
                        )
                    } else {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Logout")
                    }
                }
            } else {
                // Show error or empty state if profile is null after loading
                Text("Could not load profile information.")
            }
        }
    }
}

@Composable
fun UserProfileDetails(profile: UserProfile) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Space between profile items
    ) {
        ProfileDetailItem("Username:", profile.username)
        ProfileDetailItem("Name:", profile.name)
        ProfileDetailItem("Email:", profile.email)
        ProfileDetailItem("Role:", profile.role)
        ProfileDetailItem("Date of Birth:", profile.dateOfBirth) // TODO: Format date if needed
        ProfileDetailItem("Gender:", profile.gender)
        // Display attributes if needed
        if (profile.attributeNames.isNotEmpty()) {
            ProfileDetailItem("Attributes:", profile.attributeNames.joinToString())
        }
        // Display userProfileId only if necessary for debugging/info
        // ProfileDetailItem("Profile ID:", profile.userProfileId)
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(120.dp) // Adjust width as needed for alignment
        )
        Text(
            text = value ?: "-", // Show dash if value is null
            style = MaterialTheme.typography.bodyMedium
        )
    }
}