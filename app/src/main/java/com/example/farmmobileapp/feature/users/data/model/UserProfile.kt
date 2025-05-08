package com.example.farmmobileapp.feature.users.data.model

data class UserProfile(
    val userId: String,
    val userProfileId: String,

    val username: String,
    val email: String,
    val role: String,

    val name: String,
    val dateOfBirth: String, // Format: YYYY-MM-DD
    val gender: String,
    val attributeNames: List<String>,
)
