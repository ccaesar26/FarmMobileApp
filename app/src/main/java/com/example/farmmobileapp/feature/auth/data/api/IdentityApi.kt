package com.example.farmmobileapp.feature.auth.data.api

import com.example.farmmobileapp.feature.auth.data.model.LoginResponse
import com.example.farmmobileapp.feature.auth.data.model.RefreshTokenResponse
import com.example.farmmobileapp.util.Resource

interface IdentityApi {
    suspend fun login(email: String, password: String): Resource<LoginResponse>
    suspend fun logout(): Resource<Unit>
    suspend fun refreshToken(refreshToken: String): Resource<RefreshTokenResponse>
}