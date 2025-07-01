package com.example.farmmobileapp.feature.tasks.data.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TaskCommentDto(
    val id: String,
    val taskId: String,
    val userId: String,
    val createdAt: String,
    val comment: String
) {
    fun getCreatedAtAsMillis(): Long {
        return Instant.parse(createdAt).toEpochMilli()
    }
}
