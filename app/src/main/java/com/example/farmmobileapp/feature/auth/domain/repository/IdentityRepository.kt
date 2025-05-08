package com.example.farmmobileapp.feature.auth.domain.repository

import com.example.farmmobileapp.feature.auth.data.model.RefreshTokenResponse
import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource

interface IdentityRepository {
    suspend fun loginAndCheckRole(email: String, password: String): Resource<Boolean>
    suspend fun logout(): Resource<Unit>
    suspend fun refreshToken(refreshToken: String): Resource<RefreshTokenResponse> // Add this to the interface
}