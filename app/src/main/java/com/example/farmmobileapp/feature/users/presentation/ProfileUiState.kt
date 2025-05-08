package com.example.farmmobileapp.feature.users.presentation // Or a suitable package

import com.example.farmmobileapp.feature.users.data.model.UserProfile // Import your UserProfile model

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String? = null,
    val isLoggingOut: Boolean = false,
    val logoutSuccess: Boolean = false,
    val logoutError: String? = null
)