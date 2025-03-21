package com.example.farmmobileapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.farmmobileapp.data.storage.AuthenticationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
): ViewModel() {

    val isAuthenticated: StateFlow<Boolean> = authenticationManager.isAuthenticated
}