package com.example.farmmobileapp.feature.auth.data.api

import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.auth.data.model.LoginResponse
import com.example.farmmobileapp.feature.auth.data.model.RefreshTokenResponse
import com.example.farmmobileapp.util.Resource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorIdentityApi(private val httpClient: HttpClient) : IdentityApi {
    private val baseUrl = "api/identity"  //BuildConfig.BASE_URL

    override suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val response = httpClient.post("$baseUrl/login") { // Your backend login endpoint
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password)) // Request body
            }
            if (response.status.isSuccess()) {
                val loginResponse = response.body<LoginResponse>()
                Resource.Success(loginResponse)
            } else {
                Resource.Error("Login failed: ${response.status.description}") // More descriptive error
            }
        } catch (e: Exception) {
            Resource.Error("Login failed: Network error - ${e.message}") // Handle network errors
        }
    }

    override suspend fun logout(): Resource<Unit> {
        return try {
            val response = httpClient.post("$baseUrl/logout") { // Your backend logout endpoint
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Logout failed: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Logout failed: Network error - ${e.message}")
        }
    }

    override suspend fun refreshToken(refreshToken: String): Resource<RefreshTokenResponse> {
        return try {
            val response = httpClient.post("$baseUrl/refresh-token") { // Your backend refresh token endpoint
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken)) // Send refresh token in the body
            }
            if (response.status.isSuccess()) {
                val refreshTokenResponse = response.body<RefreshTokenResponse>()
                Resource.Success(refreshTokenResponse)
            } else {
                Resource.Error("Failed to refresh token: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to refresh token: Network error - ${e.message}")
        }
    }
}