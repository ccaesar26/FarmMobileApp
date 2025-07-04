package com.example.farmmobileapp.feature.reports.presentation

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
//import androidx.compose.material3.ExposedDropdownMenuBoxScope.menuAnchor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.fields.data.model.Field
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper function to create a temp file URI using FileProvider
fun Context.createImageFileUri(): Uri {
    // Create an image file name using timestamp
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    // Get the directory (using cache path defined in file_paths.xml)
    val storageDir = File(cacheDir, "images") // Corresponds to <cache-path path="images/"/>
    storageDir.mkdirs() // Ensure the directory exists
    val imageFile = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg",        /* suffix */
        storageDir     /* directory */
    )

    // Get the content URI using the FileProvider
    // IMPORTANT: Use the same authority string as declared in AndroidManifest.xml
    val authority =
        "${BuildConfig.APPLICATION_ID}.fileprovider" // Or hardcode if necessary, but BuildConfig is better
    return FileProvider.getUriForFile(
        this,
        authority,
        imageFile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navController: NavController,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val fields by viewModel.fields.collectAsState() // Collect fields list
    val isFieldsLoading by viewModel.isFieldsLoading.collectAsState() // Collect loading state
    val context = LocalContext.current

    // Temporary state to hold the URI for the camera capture
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // State for text fields, using rememberSaveable for process death survival
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    // --- State for Dropdown ---
    var isDropdownExpanded by remember { mutableStateOf(false) }
    // Store the currently selected Field object (or null)
    var selectedField by remember { mutableStateOf<Field?>(null) }

    // --- Image Picker ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.onImageSelected(uri)
        }
    )

    // --- Take Picture Launcher ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success) {
                // Image captured successfully, update VM with the temp URI
                tempCameraUri?.let { uri ->
                    viewModel.onImageSelected(uri)
                }
            } else {
                // Handle failure (optional: show toast or log)
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
                tempCameraUri = null // Reset temp uri if capture failed/cancelled
            }
        }
    )

    LaunchedEffect(uiState.creationSuccess) {
        if (uiState.creationSuccess) {
            navController.popBackStack()
            viewModel.resetCreationStatus() // Reset flag in VM
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Add padding around the content
            .verticalScroll(rememberScrollState()) // Make content scrollable
        ,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between elements
    ) {
        Text(
            text = "Create New Report",
            style = MaterialTheme.typography.headlineLarge,
        )
        // --- Title Input ---
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isCreating,
            // isError = title.isBlank() && (attemptedSubmit) // Optional: Add validation indication
        )

        // --- Description Input ---
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp), // Give description more space
            enabled = !uiState.isCreating,
            maxLines = 5
            // isError = description.isBlank() && (attemptedSubmit) // Optional
        )

        // --- Field Dropdown Selector ---
        // Use ExposedDropdownMenuBox for Material 3 dropdown
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = {
                // Only allow expanding if fields are loaded and not creating report
                if (!isFieldsLoading && !uiState.isCreating) {
                    isDropdownExpanded = !isDropdownExpanded
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField( // Or use TextField if you prefer non-outlined
                // Display the selected field's name, or a placeholder
                value = selectedField?.name ?: if (isFieldsLoading) "Loading Fields..." else "Select a Field",
                onValueChange = { /* Read-only, selection happens in dropdown items */ },
                readOnly = true, // Important for dropdowns
                label = { Text("Field") },
                trailingIcon = {
                    // Icon indicates it's a dropdown
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                // Modifier to attach dropdown menu behavior
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                enabled = !isFieldsLoading && !uiState.isCreating, // Disable while loading/creating
//                colors = ExposedDropdownMenuDefaults.textFieldColors() // Default M3 dropdown colors
            )

            // The actual dropdown menu content
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }, // Close when clicking outside
                modifier = Modifier
                    .heightIn(max = 200.dp) // Limit height to avoid overflow
            ) {
                if (isFieldsLoading) {
                    DropdownMenuItem( // Show loading indicator inside dropdown
                        text = { CircularProgressIndicator(Modifier.size(24.dp)) },
                        onClick = { /* No action */ },
                        modifier = Modifier.align(Alignment.CenterHorizontally) // Doesn't work well here
                    )
                } else if (fields.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No fields available") },
                        onClick = { isDropdownExpanded = false },
                        enabled = false
                    )
                } else {
                    // Create items for each field
                    fields.forEach { field ->
                        DropdownMenuItem(
                            text = { Text(field.name) }, // Display field name
                            onClick = {
                                selectedField = field // Update selected field state
                                isDropdownExpanded = false // Close the dropdown
                            }
                        )
                    }
                }
            }
        }
        // --- End Field Dropdown Selector ---

        // --- Image Selection & Preview ---
        Text("Image (Optional)", style = MaterialTheme.typography.titleMedium)

        AnimatedVisibility(visible = selectedImageUri != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(selectedImageUri)
                            .crossfade(true)
                            // .placeholder(R.drawable.ic_placeholder_image) // Optional placeholder
                            // .error(R.drawable.ic_placeholder_image) // Optional error placeholder
                            .build()
                    ),
                    contentDescription = "Selected image preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.Crop // Or ContentScale.Fit
                )
                // Button to clear selection
                IconButton(
                    onClick = { viewModel.onImageSelected(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp) // Make it slightly larger for easier tapping
                    // .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape) // Optional background
                    ,
                    enabled = !uiState.isCreating
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Clear selected image",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Button to launch image picker
        Row {
            Button(
                // Launch image taker
                onClick = {
                    try {
                        val uri = context.createImageFileUri() // Generate temp URI
                        tempCameraUri = uri // Store it for the result handler
                        takePictureLauncher.launch(uri) // Launch camera
                    } catch (ex: Exception) {
                        // Handle potential errors during file creation/URI generation
                        Toast.makeText(
                            context,
                            "Error preparing camera: ${ex.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        tempCameraUri = null
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isCreating
            ) {
                Text(if (selectedImageUri == null) "Take Image" else "Retake Image")
            }
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Button(
                onClick = { imagePickerLauncher.launch("image/*") }, // Launch image picker
                modifier = Modifier.weight(1f),
                enabled = !uiState.isCreating
            ) {
                Text(if (selectedImageUri == null) "Select Image" else "Change Image")
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push submit button towards bottom if content is short

        // --- Submit Button ---
        Row {
            FilledTonalButton(
                onClick = {
                    // Go back to previous screen
                    navController.popBackStack()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text("Cancel")
            }
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Button(
                onClick = {
                    viewModel.createReport(title, description, selectedField?.id ?: "")
                },
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && description.isNotBlank() && selectedField != null && !uiState.isCreating
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary, // Make it visible on button background
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Submit Report")
                }
            }
        }
    }

}