package com.example.farmmobileapp.feature.notifications.domain.repository

import com.example.farmmobileapp.feature.notifications.data.api.NotificationsApi
import com.example.farmmobileapp.feature.notifications.data.model.Notification
import com.example.farmmobileapp.util.Resource
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val notificationsApi: NotificationsApi
) : NotificationsRepository {

    override suspend fun getAllNotifications(): Resource<List<Notification>> {
        return notificationsApi.getAllNotifications() // Use NotificationsApi to get all notifications
    }

    override suspend fun markAsRead(notificationId: String): Resource<Unit> {
        return notificationsApi.markAsRead(notificationId) // Use NotificationsApi to mark a notification as read
    }
}