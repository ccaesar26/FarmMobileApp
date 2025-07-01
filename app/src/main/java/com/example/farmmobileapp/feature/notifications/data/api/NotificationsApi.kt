package com.example.farmmobileapp.feature.notifications.data.api

import com.example.farmmobileapp.feature.notifications.data.model.Notification
import com.example.farmmobileapp.util.Resource

interface NotificationsApi {
    suspend fun getAllNotifications(): Resource<List<Notification>>
    suspend fun markAsRead(notificationId: String): Resource<Unit>
}