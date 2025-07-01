package com.example.farmmobileapp.feature.tasks.presentation

import com.example.farmmobileapp.feature.tasks.data.model.TaskCommentDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class TaskCommentWithUser(
    val taskComment: TaskCommentDto,
    val userName: String,
) {
    fun getUserFriendlyCreatedAt(): String {
        return try {
            val instant = Instant.parse(taskComment.createdAt)
            val dateTime = instant.atZone(ZoneId.systemDefault())
            dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
        } catch (e: Exception) {
            "Unknown date"
        }
    }

}
