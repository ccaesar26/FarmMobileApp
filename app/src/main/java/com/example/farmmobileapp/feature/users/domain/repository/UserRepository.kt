package com.example.farmmobileapp.feature.users.domain.repository

import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource

interface UserRepository {
    suspend fun getMeUser(): Resource<UserRoleResponse>
}