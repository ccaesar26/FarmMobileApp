package com.example.farmmobileapp.feature.tasks.data.api

import android.util.Log
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.core.storage.TokenManager
import com.example.farmmobileapp.feature.tasks.data.model.Field
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import java.lang.System.console
import javax.inject.Inject

class KtorTasksApi @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenManager: TokenManager
) : TasksApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getMyTasks(): Resource<List<TaskDto>> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("No token available")
            val response = httpClient.get("$baseUrl/tasks/my") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val tasks = response.body<List<TaskDto>>()
                Resource.Success(tasks)
            } else {
                Resource.Error("Failed to fetch tasks: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch tasks: Network error - ${e.message}")
        }
    }

    override suspend fun getTaskById(id: String): Resource<TaskDto> {
        return try {
            val token = tokenManager.getToken() ?: return Resource.Error("No token available")
            val response = httpClient.get("$baseUrl/tasks/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                val task = response.body<TaskDto>()
                Resource.Success(task)
            } else {
                Resource.Error("Failed to fetch task: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch task: Network error - ${e.message}")
        }
    }
}