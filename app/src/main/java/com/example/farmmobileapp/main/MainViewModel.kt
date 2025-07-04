package com.example.farmmobileapp.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.core.domain.repository.MapboxTokenRepository
import com.example.farmmobileapp.core.storage.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

enum class NavigationCommand {
    NavigateToLogin
}

@HiltViewModel
class MainViewModel @Inject constructor(
    authenticationManager: AuthenticationManager,
    private val mapboxTokenRepository: MapboxTokenRepository
): ViewModel() {

    private val _mapboxToken = MutableStateFlow<String?>(null)
    val mapboxToken: StateFlow<String?> = _mapboxToken

    val isAuthenticated: StateFlow<Boolean> = authenticationManager.isAuthenticated

    // --- Navigation Event Channel ---
    private val _navigationCommand = Channel<NavigationCommand>(Channel.BUFFERED)
    val navigationCommand = _navigationCommand.receiveAsFlow() // Expose as Flow

    init {
        Log.d("MainViewModel", "MainViewModel initialized")
        // Load token when ViewModel is created AND user is authenticated
        viewModelScope.launch {
            authenticationManager.isAuthenticated.collect { hasToken ->
                if (hasToken) {
                    loadToken() // Load token only after authentication
                } else {
                    _mapboxToken.value = null // Optionally clear token if logged out
                }
            }
        }
    }

    fun loadToken() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Loading Mapbox token...")
                val token = mapboxTokenRepository.getToken()
                _mapboxToken.value = token
                Log.d("MainViewModel", "Mapbox token loaded: $token")
            } catch (e: Exception) {
                // Handle token loading error (e.g., log, show error message)
                Log.e("MainViewModel", "Error loading Mapbox token", e)
                _mapboxToken.value = null // Indicate token loading failure
            }
        }
    }

    // --- Function to trigger navigation ---
    fun requestLogoutNavigation() {
        viewModelScope.launch {
            _navigationCommand.send(NavigationCommand.NavigateToLogin)
        }
    }
}