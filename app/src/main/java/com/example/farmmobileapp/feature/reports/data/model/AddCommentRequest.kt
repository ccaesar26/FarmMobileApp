package com.example.farmmobileapp.feature.reports.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddCommentRequest(
    @SerialName("CommentText") val commentText: String,
    @SerialName("ParentCommentId") val parentCommentId: String? = null // Default to null
)