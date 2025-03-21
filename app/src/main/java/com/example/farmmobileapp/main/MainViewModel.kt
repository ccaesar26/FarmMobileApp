package com.example.farmmobileapp.main

import androidx.lifecycle.ViewModel
import com.example.farmmobileapp.core.storage.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
): ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = authenticationManager.isAuthenticated
}