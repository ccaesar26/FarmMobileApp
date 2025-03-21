package com.example.farmmobileapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRoleResponse(
    val username: String,
    val email: String,
    val role: String
)