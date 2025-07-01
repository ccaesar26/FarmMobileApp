package com.example.farmmobileapp.feature.notifications.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val notificationType: String,
    val sourceEntityId: String,
    val triggeringUserId: String,
    val timestamp: String,
    val targetFarmId: String,
    val targetUserId: String?,
    val isRead: Boolean,
)

data class NotificationDisplay(
    val id: String,
    val notificationType: String,
    val sourceEntityId: String,
    val triggeringUserId: String,
    val timestamp: String,
    val targetFarmId: String,
    val targetUserId: String?,
    val isRead: Boolean,
    val message: String
) {
    companion object {
        fun fromNotification(notification: Notification): NotificationDisplay {
            val message = when (notification.notificationType) {
                "NewTask" -> "You have a new task assigned."
                else -> "Notification of type ${notification.notificationType} from ${notification.sourceEntityId}."
            }

            return NotificationDisplay(
                id = notification.id,
                notificationType = notification.notificationType,
                sourceEntityId = notification.sourceEntityId,
                triggeringUserId = notification.triggeringUserId,
                timestamp = notification.timestamp,
                targetFarmId = notification.targetFarmId,
                targetUserId = notification.targetUserId,
                isRead = notification.isRead,
                message = message
            )
        }

        fun fromNotificationDto(notificationDto: NotificationDto): NotificationDisplay {
            val message = when (notificationDto.type) {
                "NewTask" -> "You have a new task assigned."
                else -> "Notification of type ${notificationDto.type} from ${notificationDto.entityId}."
            }
            return NotificationDisplay(
                id = notificationDto.id,
                notificationType = notificationDto.type,
                sourceEntityId = notificationDto.entityId,
                triggeringUserId = notificationDto.triggeringUserId,
                timestamp = notificationDto.timestamp,
                targetFarmId = notificationDto.farmId,
                targetUserId = notificationDto.targetUserId,
                isRead = notificationDto.isRead,
                message = message
            )
        }
    }
}