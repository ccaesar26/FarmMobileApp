package com.example.farmmobileapp.feature.tasks.data.api

import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorTasksApi @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenRepository: TokenRepository
) : TasksApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getMyTasks(): Resource<List<TaskDto>> {
        return try {
            val token = tokenRepository.getAccessToken() ?: return Resource.Error("No token available")
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
            val token = tokenRepository.getAccessToken() ?: return Resource.Error("No token available")
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

    override suspend fun updateStatus(taskId: String, status: TaskStatus): Resource<Unit> {
        return try {
            val token = tokenRepository.getAccessToken() ?: return Resource.Error("No token available")
            val response = httpClient.put("$baseUrl/tasks/$taskId/status") { // Use httpClient.put and construct URL
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(status.ordinal) // Set the TaskStatus enum as the request body
            }
            if (response.status.isSuccess()) {
                Resource.Success(Unit) // Return Resource.Success<Unit> for successful update (NoContent)
            } else {
                Resource.Error("Failed to update task status: ${response.status.description}")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to update task status: Network error - ${e.message}")
        }
    }
}