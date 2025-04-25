package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.core.storage.TokenRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.* // Or Android engine
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.call.body

// Data class for your refresh token response (adjust structure as needed)
@kotlinx.serialization.Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String // Optional: Your backend might issue a new refresh token
)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true // Optional: for easier debugging
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        tokenRepository: TokenRepository,
        json: Json
    ): HttpClient {
        return HttpClient(CIO) { // Or Android engine: HttpClient(Android)

            expectSuccess = true // Optional: Fail if status code is not 2xx

            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL // Adjust log level as needed (INFO, BODY, etc.)
            }

            install(Auth) {
                bearer {
                    // --- 1. Load Tokens ---
                    // This is called BEFORE every request that needs authentication
                    loadTokens {
                        // Load tokens from your secure storage
                        val accessToken = tokenRepository.getAccessToken()
                        val refreshToken = tokenRepository.getRefreshToken()
                        if (accessToken != null && refreshToken != null) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null // No tokens available, request won't be authenticated
                        }
                    }

                    // --- 2. Refresh Tokens ---
                    // This is called when a request receives a 401 Unauthorized
                    // AND the initial loadTokens provided tokens.
                    refreshTokens {
                        // Note: This block runs synchronized by the Auth plugin
                        println("Attempting to refresh tokens...")
                        val currentRefreshToken = tokenRepository.getRefreshToken() ?: return@refreshTokens null // No refresh token, cannot refresh

                        // Create a separate Ktor client instance ONLY for the refresh call
                        // to avoid recursion or using the potentially failing Auth setup of the main client.
                        val refreshTokenClient = HttpClient(CIO) { // Or Android
                            install(ContentNegotiation) { json(json) }
                            // No Auth feature here!
                            expectSuccess = false // Handle errors manually for refresh endpoint
                        }

                        try {
                            val response: RefreshTokenResponse = refreshTokenClient.post("YOUR_BACKEND_BASE_URL/api/auth/refresh") { // Replace with your actual refresh endpoint
                                contentType(ContentType.Application.Json)
                                // Send the refresh token (adjust based on your backend's expectation)
                                // Example: Sending as JSON body
                                setBody(mapOf("refreshToken" to currentRefreshToken))
                                // Example: Sending as header
                                // header("Authorization-Refresh", "Bearer $currentRefreshToken")
                            }.body() // Assuming ktor throws on non-2xx if expectSuccess=true

                            // Persist the new tokens
                            tokenRepository.saveAccessToken(response.accessToken)
                            tokenRepository.saveRefreshToken(response.refreshToken)
                            println("Tokens refreshed successfully.")

                            // Return the new tokens to the Auth feature
                            BearerTokens(response.accessToken, response.refreshToken)

                        } catch (e: Exception) {
                            // Handle different errors (network, server error on refresh endpoint, invalid refresh token)
                            println("Failed to refresh tokens: ${e.message}")
                            // Critical: If refresh fails (e.g., refresh token expired/invalid), clear tokens
                            // and signal logout is needed.
                            tokenRepository.clearAccessToken()
                            tokenRepository.clearRefreshToken()
                            // TODO: Trigger a global logout state change (e.g., via a SharedFlow/Channel)
                            null // Return null to indicate refresh failed
                        } finally {
                            refreshTokenClient.close() // Close the temporary client
                        }
                    }

                    // --- 3. Send Without Request? (Optional) ---
                    // Should Ktor send the request even if loadTokens returns null?
                    // Usually false for bearer tokens, you want requests to fail if no token.
                    sendWithoutRequest { request ->
                        // Define which paths DON'T need authentication (e.g., login, register, maybe refresh itself)
                        // return true if the request path should proceed without a token
                        val path = request.url.encodedPath
                        path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")
                        // Important: Do NOT add the refresh path here if refreshTokens block handles it
                    }
                }
            }

            // Optional: Default request configuration
            defaultRequest {
                url("YOUR_BACKEND_BASE_URL/") // Set your base URL
                contentType(ContentType.Application.Json)
            }
        }
    }
}