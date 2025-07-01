package com.example.farmmobileapp.feature.users.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Import Repositories and Models
import com.example.farmmobileapp.feature.auth.domain.repository.IdentityRepository
import com.example.farmmobileapp.feature.users.domain.repository.UserRepository
import com.example.farmmobileapp.main.MainViewModel
import com.example.farmmobileapp.util.Resource // Import your Resource wrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val identityRepository: IdentityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        if (_uiState.value.isLoading) return // Avoid concurrent loads

        Log.d("ProfileViewModel", "Loading user profile")

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d("ProfileViewModel", "Fetching user profile from repository")
            when (val result = userRepository.getUser()) { // Call the correct function
                is Resource.Success -> {
                    Log.d("ProfileViewModel", "User profile loaded successfully")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userProfile = result.data,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    Log.d("ProfileViewModel", "Error loading user profile: ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userProfile = null, // Clear profile on error
                            error = result.message ?: "Failed to load profile"
                        )
                    }
                }
                // Handle Loading state from Resource if applicable
                is Resource.Loading -> {
                    // Handled by isLoading = true above
                }
            }
        }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return // Prevent double logout

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true, logoutError = null, logoutSuccess = false) }
            when (val result = identityRepository.logout()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoggingOut = false, logoutSuccess = true, logoutError = null)
                    }
                    Log.d("ProfileViewModel", "Logout successful")
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            logoutSuccess = false,
                            logoutError = result.message ?: "Logout failed"
                        )
                    }
                }
                is Resource.Loading -> {
                    // Handled by isLoggingOut = true
                }
            }
        }
    }

    // Optional: Function to reset logout state if needed after UI reacts
    fun resetLogoutStatus() {
        _uiState.update { it.copy(logoutSuccess = false, logoutError = null) }
    }

    // Optional: Function to clear general errors if needed
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}