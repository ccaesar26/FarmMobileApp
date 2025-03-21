package com.example.farmmobileapp.data.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthenticationManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            checkInitialAuthenticationStatus()
        }
    }

    private suspend fun checkInitialAuthenticationStatus() {
        val token = tokenManager.getToken() // Synchronous token check on initialization
        _isAuthenticated.value = !token.isNullOrBlank()
    }

    fun setAuthenticated(isAuthenticated: Boolean) {
        _isAuthenticated.value = isAuthenticated
    }
}