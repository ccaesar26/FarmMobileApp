package com.example.farmmobileapp.feature.reports.data.model

data class ReportCreatedEventPayload(
    val reportId: String? = null, // Nullable String for Guid?
    val farmId: String? = null,   // Nullable String for Guid?
    val createdByUserId: String? = null // Nullable String for Guid?
    // Add other fields if your ReportCreatedEvent C# class has more
)