package com.example.farmmobileapp.core.storage

import com.example.farmmobileapp.feature.auth.data.api.IdentityApi
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthenticationManager @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val identityApi: IdentityApi,
    private val usersApi: UsersApi
) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            checkInitialAuthenticationStatus()
        }
    }

    private suspend fun checkInitialAuthenticationStatus() {
        val token = tokenRepository.getAccessToken()
        if (token != null) {
            _isAuthenticated.value = true
            val roleResult = usersApi.getMe()
            if (roleResult is Resource.Error) {
                val refreshToken = tokenRepository.getRefreshToken()
                if (refreshToken != null) {
                    when (val response = identityApi.refreshToken(refreshToken)) {
                        is Resource.Success -> {
                            val newAccessToken = response.data?.accessToken
                            if (newAccessToken != null) {
                                tokenRepository.saveAccessToken(newAccessToken)
                                _isAuthenticated.value = true
                            } else {
                                _isAuthenticated.value = false
                            }
                        }

                        is Resource.Error -> {
                            _isAuthenticated.value = false
                        }

                        is Resource.Loading<*> -> {}
                    }
                } else {
                    _isAuthenticated.value = false
                    tokenRepository.clearAccessToken()
                }
            }
        } else {
            _isAuthenticated.value = false
        }
    }

    fun setAuthenticated(isAuthenticated: Boolean) {
        _isAuthenticated.value = isAuthenticated
    }
}