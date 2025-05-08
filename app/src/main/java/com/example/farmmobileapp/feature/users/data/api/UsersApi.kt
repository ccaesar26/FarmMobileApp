package com.example.farmmobileapp.feature.users.data.api

import com.example.farmmobileapp.feature.users.data.model.UserNameResponse
import com.example.farmmobileapp.feature.users.data.model.UserResponse
import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource

interface UsersApi {
    suspend fun getMe(): Resource<UserRoleResponse>
    suspend fun getUser(): Resource<UserResponse>
    suspend fun getUsernameById(userId: String): Resource<UserNameResponse>
}