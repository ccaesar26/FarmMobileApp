package com.example.farmmobileapp.feature.reports.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReportRequest(
    @SerialName("Title") val title: String, // Match backend casing if necessary
    @SerialName("Description") val description: String,
    @SerialName("ImageUrl") val imageUrl: String?, // Make nullable if optional
    @SerialName("FieldId") val fieldId: String
)