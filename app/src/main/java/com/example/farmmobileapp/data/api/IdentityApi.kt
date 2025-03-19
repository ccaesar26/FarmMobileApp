package com.example.farmmobileapp.data.api

import com.example.farmmobileapp.data.model.LoginResponse
import com.example.farmmobileapp.util.Resource

interface IdentityApi {
    suspend fun login(email: String, password: String): Resource<LoginResponse>
}