package com.example.farmmobileapp.feature.reports.presentation

import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.farmmobileapp.feature.reports.data.model.CommentWithUsername
import com.example.farmmobileapp.feature.reports.data.model.Report
import com.example.farmmobileapp.util.extensions.formatIsoInstantToLocal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class) // Added for imePadding
@Composable
fun ReportDetailScreen(
    navController: NavController, // Still useful for navigation logic
    viewModel: ReportDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var commentText by remember { mutableStateOf("") }

    // TODO: Implement alternative error/success feedback (Toast, Dialog, etc.)
    // LaunchedEffect(...) { ... } for snackbar replacement

    // Root Column to arrange scrollable content and fixed bottom bar
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Add padding for System Bars (Status bar, Navigation bar)
            // This prevents content from drawing under them.
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {

        // --- Scrollable Content Area ---
        // This Column takes up all available space *except* for the input row below it.
        Column(
            modifier = Modifier
                .weight(1f) // <-- Key change: Takes available vertical space
                .fillMaxWidth() // Ensure it spans width
                .verticalScroll(rememberScrollState()) // Content within scrolls
                .padding(horizontal = 16.dp) // Horizontal padding for content
                .padding(top = 16.dp) // Padding at the top
        ) {
            // --- Report Details Section ---
            if (uiState.isReportLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            } else if (uiState.report != null) {
                ReportDetailsContent(report = uiState.report!!)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            } else if (uiState.error == null) {
                Text("Loading report...")
            }
            // Error display
            uiState.error?.let {
                if (uiState.report == null) {
                    Text("Error loading report: $it", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                }
            }

            // --- Comments Section ---
            Text("Comments", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (uiState.areCommentsLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (uiState.comments.isEmpty()) {
                Text("No comments yet.")
            } else {
                // Comments laid out statically
                Column(modifier = Modifier.fillMaxWidth()) {
                    uiState.comments.forEach { comment ->
                        CommentItem(comment)
                        HorizontalDivider()
                    }
                }
            }

            // Add space at the bottom of the scrollable content
            Spacer(Modifier.height(16.dp))

        } // --- End of Scrollable Content Column ---


        // --- Sticky Bottom Input Row ---
        // This Row is placed *after* the weighted Column, so it appears at the bottom.
        HorizontalDivider() // Divider above the input row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                ) // Padding for the input row
                .imePadding() // Adjusts padding when keyboard is visible
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Add a comment") },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                enabled = !uiState.isAddingComment
            )
            Spacer(Modifier.width(8.dp))
            FilledIconButton(
                onClick = {
                    viewModel.addComment(commentText)
                    commentText = "" // Optimistic clear
                },
                enabled = commentText.isNotBlank() && !uiState.isAddingComment,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                if (uiState.isAddingComment) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Send,
                        contentDescription = "Add Comment"
                    )
                }
            }
        } // --- End of Sticky Bottom Input Row ---

    } // --- End of Root Column ---
}

@Composable
fun ReportDetailsContent(report: Report) { // Specify full path if naming clash
    val context = LocalContext.current

    // --- State for decoded image bytes (optional but cleaner for Coil) ---
    // Use remember with the report.imageData as key to re-decode if it changes
    val imageBytes: ByteArray? = remember(report.imageData) {
        if (!report.imageData.isNullOrBlank()) {
            try {
                // Decode Base64 string back to byte array
                Base64.decode(report.imageData, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                // Handle invalid Base64 string
                println("Error decoding Base64 image data: ${e.message}")
                null
            }
        } else {
            null
        }
    }

    Column {
        Text(report.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        // --- Display Image if available ---
        if (imageBytes != null) {
            // Use Coil 3's AsyncImage to load the ByteArray
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageBytes) // Load directly from byte array
                    .crossfade(true)
                    // Add placeholders/error drawables if desired
                    // .placeholder(R.drawable.placeholder)
                    // .error(R.drawable.error_image)
                    .build(),
                contentDescription = "Report Image for ${report.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Adjust height as needed
                    .padding(vertical = 8.dp), // Add some padding around the image
                contentScale = ContentScale.Fit // Or ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp)) // Add space after the image
        }

        Text("Description:", style = MaterialTheme.typography.titleMedium)
        Text(report.description)
        Spacer(Modifier.height(8.dp))
        Text("Status: ${report.status}") // Map status Int to readable string if needed
        Text("Created At: ${report.createdAt.formatIsoInstantToLocal()}") // Format date/time
        // Add other relevant fields (FieldId, FarmId etc.)
    }
}

@Composable
fun CommentItem(comment: CommentWithUsername) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(comment.comment.commentText, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "By ${comment.username} at ${comment.comment.createdAt.formatIsoInstantToLocal()}", // Shorten UserID for display, Format date
            style = MaterialTheme.typography.labelSmall
        )
    }
}