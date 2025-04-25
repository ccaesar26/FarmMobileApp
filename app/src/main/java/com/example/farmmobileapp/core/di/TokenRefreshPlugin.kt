package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.core.storage.AuthenticationManager
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.auth.domain.repository.IdentityRepository
//import com.example.farmmobileapp.feature.auth.domain.repository.RefreshTokenResponse // Assuming this exists
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshPlugin @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val identityRepository: IdentityRepository,
    private val authenticationManager: AuthenticationManager
) : HttpClientPlugin<Unit, TokenRefreshPlugin> {

    override val key: AttributeKey<TokenRefreshPlugin> = AttributeKey("TokenRefreshPlugin")

    override fun prepare(block: Unit.() -> Unit) = this

    override fun install(plugin: TokenRefreshPlugin, scope: HttpClient) {
//        scope.plugin(HttpSend).intercept { request ->
//            val originalCall = execute(request)
//
//            if (originalCall.response.status == HttpStatusCode.Unauthorized) {
//                val refreshToken = tokenManager.getRefreshToken()
//                val accessToken = tokenManager.getToken()
//
//                if (!refreshToken.isNullOrBlank() && !accessToken.isNullOrBlank()) {
//                    runBlocking {
//                        val refreshResult = identityRepository.refreshToken(refreshToken)
//                        when (refreshResult) {
//                            is com.example.farmmobileapp.util.Resource.Success -> {
//                                val newToken = refreshResult.data
//                                newToken?.accessToken?.let { tokenManager.saveToken(it) }
//                                newToken?.refreshToken?.let { tokenManager.saveRefreshToken(it) }
//                                authenticationManager.setAuthenticated(true)
//
//                                val newRequest = request.newBuilder()
//                                    .header(HttpHeaders.Authorization, "Bearer ${newToken?.accessToken}")
//                                    .build()
//                                return@intercept execute(newRequest)
//                            }
//                            is com.example.farmmobileapp.util.Resource.Error -> {
//                                tokenManager.clearToken()
//                                tokenManager.clearRefreshToken()
//                                authenticationManager.setAuthenticated(false)
//                                // Potentially redirect to login here or handle in UI
//                            }
//                            is com.example.farmmobileapp.util.Resource.Loading -> {
//                                // Handle loading state if needed
//                            }
//                        }
//                    }
//                }
//            }
//            originalCall
//        }
    }
//
//    companion object : AttributeKey<TokenRefreshPlugin> {
//        override val key: PluginKey<TokenRefreshPlugin> = PluginKey("TokenRefreshPlugin")
//    }
}