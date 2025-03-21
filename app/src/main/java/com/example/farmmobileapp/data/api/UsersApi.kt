package com.example.farmmobileapp.data.api

import com.example.farmmobileapp.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource

interface UsersApi {
    suspend fun getMe(): Resource<UserRoleResponse>
}