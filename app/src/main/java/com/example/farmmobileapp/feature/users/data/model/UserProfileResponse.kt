package com.example.farmmobileapp.feature.users.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val dateOfBirth: String, // Format: YYYY-MM-DD
    val gender: String,
    val attributeNames: List<String>
)
