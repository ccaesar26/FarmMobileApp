package com.example.farmmobileapp.feature.reports.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val status: Int, // Consider using an Enum if statuses are well-defined
    val fieldId: String,
    val farmId: String,
    val createdByUserId: String,
    val createdAt: String, // Keep as String for simplicity, or use kotlinx-datetime library for Instant/LocalDateTime
    val updatedAt: String?,
    val commentCount: Int
) {
    // You could add helper computed properties here if needed
    // val formattedCreatedAt: String get() = ... // Example
}