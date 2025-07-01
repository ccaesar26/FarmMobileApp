package com.example.farmmobileapp.feature.tasks.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTaskCommentDto(
    val taskId: String,
    val comment: String
)
