package com.example.farmmobileapp.data.api

import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.data.model.UserRoleResponse
import com.example.farmmobileapp.data.storage.TokenManager
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorUsersApi @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : UsersApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getMe(): Resource<UserRoleResponse> {
        return try {
            val token = tokenManager.getToken()
                ?: return Resource.Error("No token available") // Handle case where token is missing (shouldn't happen if called after login)

            val response = httpClient.get("$baseUrl/users/me") { // Your backend /users/me endpoint
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token") // Add Authorization header with JWT token
            }
            if (response.status.isSuccess()) {
                val userRoleResponse = response.body<UserRoleResponse>()
                Resource.Success(userRoleResponse)
            } else {
                Resource.Error("Failed to get user profile: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to get user profile: Network error - ${e.message}")
        }
    }
}