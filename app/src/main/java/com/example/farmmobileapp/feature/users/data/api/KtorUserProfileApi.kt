package com.example.farmmobileapp.feature.users.data.api

import com.example.farmmobileapp.feature.users.data.model.UserProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class KtorUserProfileApi @Inject constructor(
    private val httpClient: HttpClient
) : UserProfileApi {
    private val baseUrl = "api/user-profile"

    override suspend fun getUserProfile(): UserProfileResponse {
        return try {
            val response = httpClient.get(baseUrl) // Your backend /user-profile/me endpoint
            response.body<UserProfileResponse>()
        } catch (e: Exception) {
            throw Exception("Failed to get user profile: ${e.message}")
        }
    }
}