package com.example.farmmobileapp.feature.reports.presentation

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.feature.fields.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.reports.data.model.CreateReportRequest
import com.example.farmmobileapp.feature.reports.domain.repository.ReportsRepository
import com.example.farmmobileapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CreateReportUiState(
    val isCreating: Boolean = false,
    val error: String? = null,
    val creationSuccess: Boolean = false
)

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val reportRepository: ReportsRepository,
    private val fieldsRepository: FieldsRepository,
    private val contentResolver: ContentResolver // Inject ContentResolver via Hilt (@ApplicationContext)
    // See Hilt setup below if not already done
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateReportUiState())
    val uiState: StateFlow<CreateReportUiState> = _uiState.asStateFlow()

    // State to hold the selected image Uri (e.g., from an image picker)
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    // List of all fields (from user's farm)
    private val _fields = MutableStateFlow<List<Field>>(emptyList())
    val fields: StateFlow<List<Field>> = _fields.asStateFlow()

    private val _isFieldsLoading = MutableStateFlow(false)
    val isFieldsLoading: StateFlow<Boolean> = _isFieldsLoading.asStateFlow()

    init {
        loadFields() // Load fields when ViewModel is created
    }

    private fun loadFields() {
        viewModelScope.launch {
            if (_isFieldsLoading.value) return@launch // Avoid concurrent loads
            try {
                _isFieldsLoading.value = true
                val result = fieldsRepository.getFields()
                if (result is Resource.Success) {
                    _fields.value = result.data ?: emptyList()
                } else {
                    _uiState.update { it.copy(error = "Failed to load fields") }
                }
                _isFieldsLoading.value = false
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error loading fields: ${e.message}") }
                _isFieldsLoading.value = false
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun createReport(title: String, description: String, fieldId: String) {
        val imageUri = _selectedImageUri.value

        // Basic validation
        if (title.isBlank() || description.isBlank() || fieldId.isBlank()) {
            _uiState.update { it.copy(error = "Title, Description, and Field ID are required.") }
            return
        }
        if (_uiState.value.isCreating) return // Prevent double submission

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, error = null, creationSuccess = false) }

            var imageBase64String: String? = null
            var imageMimeType: String? = null

            try {
                // --- 1. Upload Image using PublicStorageService (if selected) ---
                if (imageUri != null) {
                    // Perform byte reading and encoding off the main thread
                    withContext(Dispatchers.IO) {
                        val imageBytes: ByteArray? = getImageBytesFromUri(imageUri)
                        imageMimeType = getMimeTypeFromUri(imageUri) // Can potentially stay on calling thread

                        if (imageBytes != null && imageMimeType != null) {
                            // Encode the bytes to Base64 String
                            imageBase64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP) // Or Base64.DEFAULT
                        } else {
                            // Throw an error or update state if processing failed
                            throw Exception("Could not process selected image data or get MIME type.")
                        }
                    } // End withContext(Dispatchers.IO)
                } // else: No image selected, imageBase64String and imageMimeType remain null

                // --- Create Request DTO ---
                val request = CreateReportRequest(
                    title = title,
                    description = description,
                    imageDataBase64 = imageBase64String, // Pass the Base64 string or null
                    imageMimeType = imageMimeType,       // Pass the MIME type or null
                    fieldId = fieldId                    // Pass fieldId or null
                )

                // --- Call Backend Repository ---
                reportRepository.createReport(request) // This now sends the JSON with Base64 data

                // --- Success ---
                _uiState.update { it.copy(isCreating = false, creationSuccess = true) }
                _selectedImageUri.value = null // Clear selected image

            } catch (e: Exception) { // Catch errors from image processing or network call
                _uiState.update {
                    Log.d("CreateReportViewModel", "Error: ${e.message}")
                    it.copy(isCreating = false, error = "Operation failed: ${e.message}")
                }
                // Log e
            }
        }
    }

    // Helper to get bytes from Uri using ContentResolver
    private fun getImageBytesFromUri(uri: Uri): ByteArray? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            // Log error
            null
        }
    }

    // Helper to get MIME type from Uri
    private fun getMimeTypeFromUri(uri: Uri): String? {
        return contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetCreationStatus() {
        _uiState.update { it.copy(creationSuccess = false) }
    }
}
