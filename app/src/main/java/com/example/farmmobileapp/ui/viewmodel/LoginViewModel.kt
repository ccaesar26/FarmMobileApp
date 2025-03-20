package com.example.farmmobileapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.R
import com.example.farmmobileapp.data.api.IdentityApi
import com.example.farmmobileapp.data.storage.TokenManager
import com.example.farmmobileapp.util.Resource
import com.example.farmmobileapp.util.StringResourcesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val identityApi: IdentityApi,
    private val tokenManager: TokenManager,
    private val stringResourcesHelper: StringResourcesHelper
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun login() {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            when (val result = identityApi.login(email.value, password.value)) {
                is Resource.Success -> {
                    tokenManager.saveToken(result.data?.token ?: "") // Save JWT token
                    _loginState.value = LoginState.Success // Indicate successful login
                    // Navigation to MainScreen will be handled in MainActivity or Navigation Component
                }
                is Resource.Error -> {
                    _loginState.value = LoginState.Error(
                        result.message ?: stringResourcesHelper.getString(R.string.login_error_generic) // Generic error message
                    )
                }
                is Resource.Loading<*> -> {
                    // Do nothing, loading state is already set
                }
            }
        }
    }

    sealed class LoginState {
        data object Idle : LoginState()
        data object Loading : LoginState()
        data object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}