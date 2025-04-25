package com.example.farmmobileapp.feature.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
)