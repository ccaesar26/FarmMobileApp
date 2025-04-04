package com.example.farmmobileapp.feature.auth.domain.repository

import com.example.farmmobileapp.util.Resource

interface IdentityRepository {
    suspend fun loginAndCheckRole(email: String, password: String): Resource<Boolean>
}