package com.example.farmmobileapp.feature.reports.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReportRequest(
    @SerialName("Title") val title: String, // Match backend casing if necessary
    @SerialName("Description") val description: String,
    @SerialName("ImageData") val imageDataBase64: String?, // Send bytes as Base64 encoded String
    @SerialName("ImageMimeType") val imageMimeType: String?,
    @SerialName("FieldId") val fieldId: String
)