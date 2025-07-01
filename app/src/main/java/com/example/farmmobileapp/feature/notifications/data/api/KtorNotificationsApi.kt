package com.example.farmmobileapp.feature.notifications.data.api

import com.example.farmmobileapp.feature.notifications.data.model.Notification
import com.example.farmmobileapp.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class KtorNotificationsApi @Inject constructor(
    private val httpClient: HttpClient,
) : NotificationsApi {
    private val baseUrl = "api/notification"

    override suspend fun getAllNotifications(): Resource<List<Notification>> {
        return try {
            val notificationDtos = httpClient.get("$baseUrl/byUser").body<List<Notification>>() // Ensure backend returns List<NotificationDto>
            Resource.Success(notificationDtos)
        } catch (e: Exception) {
            Resource.Error("Failed to load notifications: ${e.message}")
        }
    }

    override suspend fun markAsRead(notificationId: String): Resource<Unit> {
        return try {
            httpClient.post("$baseUrl/markAsRead") {
                contentType(ContentType.Application.Json)
                // Backend expects a simple string (GUID) in the body for markAsRead
                // Ensure Ktor client is configured to serialize simple strings as JSON body if needed
                // Or adjust backend to expect an object like { "notificationId": "value" }
                setBody(notificationId) // This might send "notificationId" as plain text.
                // If backend needs JSON, send: setBody(mapOf("notificationId" to notificationId))
                // or a dedicated DTO.
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to mark as read: ${e.message}; for notificationId: $notificationId")
        }
    }
}