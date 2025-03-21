package com.example.farmmobileapp.feature.auth.data.api

import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.auth.data.model.LoginResponse
import com.example.farmmobileapp.util.Resource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorIdentityApi(private val httpClient: HttpClient) : IdentityApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val response = httpClient.post("$baseUrl/identity/login") { // Your backend login endpoint
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
}