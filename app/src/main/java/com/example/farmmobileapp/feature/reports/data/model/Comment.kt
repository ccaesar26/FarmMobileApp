package com.example.farmmobileapp.feature.reports.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val reportId: String,
    val parentCommentId: String?,
    val userId: String,
    val commentText: String,
    val createdAt: String
)