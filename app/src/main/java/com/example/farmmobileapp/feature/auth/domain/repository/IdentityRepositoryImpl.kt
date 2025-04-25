package com.example.farmmobileapp.feature.auth.domain.repository

import com.example.farmmobileapp.R
import com.example.farmmobileapp.core.storage.AuthenticationManager
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.auth.data.api.IdentityApi
import com.example.farmmobileapp.feature.auth.data.model.RefreshTokenResponse
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.util.Resource
import com.example.farmmobileapp.util.StringResourcesHelper
import javax.inject.Inject

class IdentityRepositoryImpl @Inject constructor(
    private val identityApi: IdentityApi,
    private val usersApi: UsersApi,
    private val tokenRepository: TokenRepository,
    private val authenticationManager: AuthenticationManager,
    private val stringResourcesHelper: StringResourcesHelper
) : IdentityRepository {

    override suspend fun loginAndCheckRole(email: String, password: String): Resource<Boolean> {
        when (val result = identityApi.login(email, password)) {
            is Resource.Success -> {
                val accessToken = result.data?.accessToken
                val refreshToken = result.data?.refreshToken

                if (accessToken.isNullOrEmpty()) {
                    authenticationManager.setAuthenticated(false)
                    tokenRepository.clearAccessToken()
                    tokenRepository.clearRefreshToken()
                    return Resource.Error(stringResourcesHelper.getString(R.string.login_error_generic))
                }

                tokenRepository.saveAccessToken(accessToken)
                refreshToken?.let { tokenRepository.saveRefreshToken(it) }

                val roleResult = usersApi.getMe()
                return when (roleResult) {
                    is Resource.Success -> {
                        if (roleResult.data?.role == "Worker") {
                            authenticationManager.setAuthenticated(true)
                            Resource.Success(true)
                        } else {
                            authenticationManager.setAuthenticated(false)
                            tokenRepository.clearAccessToken()
                            Resource.Error(stringResourcesHelper.getString(R.string.login_error_role_not_worker))
                        }
                    }

                    is Resource.Error -> {
                        authenticationManager.setAuthenticated(false)
                        tokenRepository.clearAccessToken()
                        Resource.Error(
                            roleResult.message
                                ?: stringResourcesHelper.getString(R.string.login_error_user_info)
                        )
                    }

                    is Resource.Loading<*> -> {
                        // Should not happen
                        Resource.Error(stringResourcesHelper.getString(R.string.login_error_generic))
                    }
                }
            }

            is Resource.Error -> {
                authenticationManager.setAuthenticated(false)
                return Resource.Error(
                    result.message ?: stringResourcesHelper.getString(R.string.login_error_generic)
                )
            }

            is Resource.Loading<*> -> {
                // Should not happen
                return Resource.Error(stringResourcesHelper.getString(R.string.login_error_generic))
            }
        }
    }

    override suspend fun refreshToken(refreshToken: String): Resource<RefreshTokenResponse> {
        return identityApi.refreshToken(refreshToken)
    }
}