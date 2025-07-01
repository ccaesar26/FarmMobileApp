package com.example.farmmobileapp.feature.notifications.data.model

import kotlinx.serialization.Serializable

@Serializable // If you plan to store/send it locally via serialization
data class NotificationDto( // Renamed from NotificationDto for Kotlin convention
    val id: String, // Guid as String
    val type: String, // e.g., "NewTask", "NewReport", "NewReportComment"
    val entityId: String, // Guid of the related entity (Task, Report, Comment)
    val triggeringUserId: String, // Guid of the user who caused the notification
    val timestamp: String, // DateTime as String (e.g., ISO 8601)
    val farmId: String, // Guid
    val targetUserId: String?, // Guid of the user this notification is for (nullable)
    var isRead: Boolean // var so it can be updated locally
)
