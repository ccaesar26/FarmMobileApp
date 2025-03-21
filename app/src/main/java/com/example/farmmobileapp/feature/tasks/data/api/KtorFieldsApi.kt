package com.example.farmmobileapp.feature.tasks.data.api

import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.core.storage.TokenManager
import com.example.farmmobileapp.feature.tasks.data.model.Field
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

class KtorFieldsApi @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : FieldsApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getField(fieldId: String): Resource<Field> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("No token available")
            val response = httpClient.get("$baseUrl/fields/$fieldId") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val field = response.body<Field>()
                Resource.Success(field)
            } else {
                Resource.Error("Failed to fetch field: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch field: Network error - ${e.message}")
        }
    }
}