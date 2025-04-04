package com.example.farmmobileapp.core.data.preferences.model

import kotlinx.serialization.Serializable

@Serializable
data class RetrieveResponse(
    val key: String,
    val value: String,
)