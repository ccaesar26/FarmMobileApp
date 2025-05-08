package com.example.farmmobileapp.feature.fields.data.api

import android.util.Log
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorFieldsApi @Inject constructor(
    private val httpClient: HttpClient,
) : FieldsApi {
    private val baseUrl = "api/fields"

    override suspend fun getField(fieldId: String): Resource<Field> {
        return try {
            val response = httpClient.get("$baseUrl/$fieldId") {
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val field = response.body<Field>()
                Log.d("KtorFieldsApi", "Field fetched successfully: $field")
                Resource.Success(field)
            } else {
                Log.d("KtorFieldsApi", "Failed to fetch field: ${response.status.description}")
                Resource.Error("Failed to fetch field: ${response.status.description}")
            }
        } catch (e: Exception) {
            Log.e("KtorFieldsApi", "Network error: ${e.message}", e)
            Resource.Error("Failed to fetch field: Network error - ${e.message}")
        }
    }
}