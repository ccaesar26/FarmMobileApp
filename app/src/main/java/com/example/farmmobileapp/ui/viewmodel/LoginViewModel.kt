package com.example.farmmobileapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.R
import com.example.farmmobileapp.data.api.IdentityApi
import com.example.farmmobileapp.data.api.UsersApi
import com.example.farmmobileapp.data.storage.AuthenticationManager
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
    private val usersApi: UsersApi,
    private val tokenManager: TokenManager,
    private val authenticationManager: AuthenticationManager,
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
                    val token = result.data?.token

                    tokenManager.saveToken(token ?: "").apply {

                        if (token != null) {
                            when (val roleResult = usersApi.getMe()) {
                                is Resource.Success -> {
                                    val role = roleResult.data?.role

                                    if (role == "Worker") {
                                        authenticationManager.setAuthenticated(true)
                                        _loginState.value = LoginState.Success
                                    } else {
                                        authenticationManager.setAuthenticated(false)
                                        tokenManager.clearToken()
                                        _loginState.value =
                                            LoginState.Error(stringResourcesHelper.getString(R.string.login_error_role_not_worker))
                                    }
                                }

                                is Resource.Error -> {
                                    authenticationManager.setAuthenticated(false)
                                    tokenManager.clearToken()
                                    _loginState.value = LoginState.Error(
                                        roleResult.message
                                            ?: stringResourcesHelper.getString(R.string.login_error_user_info)
                                    )
                                }

                                is Resource.Loading<*> -> {
                                    // Do nothing, loading state is already set
                                }
                            }
                        } else {
                            authenticationManager.setAuthenticated(false)
                            tokenManager.clearToken()
                            _loginState.value =
                                LoginState.Error(stringResourcesHelper.getString(R.string.login_error_generic))
                        }
                    }
                }

                is Resource.Error -> {
                    authenticationManager.setAuthenticated(false)
                    _loginState.value = LoginState.Error(
                        result.message
                            ?: stringResourcesHelper.getString(R.string.login_error_generic)
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