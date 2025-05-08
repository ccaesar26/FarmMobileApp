package com.example.farmmobileapp.feature.users.data.api

import android.util.Log
import com.example.farmmobileapp.feature.users.data.model.UserNameResponse
import com.example.farmmobileapp.feature.users.data.model.UserResponse
import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorUsersApi @Inject constructor(
    private val httpClient: HttpClient,
//    private val tokenRepository: TokenRepository
) : UsersApi {
    private val baseUrl = "api/users"

    override suspend fun getMe(): Resource<UserRoleResponse> {
        return try {
//            val token = tokenRepository.getAccessToken()
//                ?: return Resource.Error("No token available") // Handle case where token is missing (shouldn't happen if called after login)

            val response = httpClient.get("$baseUrl/me") { // Your backend /users/me endpoint
                contentType(ContentType.Application.Json)
//                header(HttpHeaders.Authorization, "Bearer $token") // Add Authorization header with JWT token
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

    override suspend fun getUser(): Resource<UserResponse> {
        return try {
            Log.d("KtorUsersApi", "Fetching user profile from API")
            val response = httpClient.get("$baseUrl/user") { // Your backend /users/me endpoint
                contentType(ContentType.Application.Json)
            }
            Log.d("KtorUsersApi", "Response status: ${response.status}")
            if (response.status.isSuccess()) {
                Log.d("KtorUsersApi", "Response is successful")
                val userResponse = response.body<UserResponse>()
                Log.d("KtorUsersApi", "User profile: $userResponse")
                Resource.Success(userResponse)
            } else {
                Log.e("KtorUsersApi", "Error fetching user profile: ${response.status.description}")
                Resource.Error("Failed to get user profile: ${response.status.description}")
            }
        } catch (e: Exception) {
            Log.e("KtorUsersApi", "Network error: ${e.message}")
            Resource.Error("Failed to get user profile: Network error - ${e.message}")
        }
    }

    override suspend fun getUsernameById(userId: String): Resource<UserNameResponse> {
        return try {
            val response = httpClient.get("$baseUrl/username/$userId") { // Your backend /users/{userId} endpoint
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val userNameResponse = response.body<UserNameResponse>()
                Resource.Success(userNameResponse)
            } else {
                Resource.Error("Failed to get user profile: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to get user profile: Network error - ${e.message}")
        }
    }
}