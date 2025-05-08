package com.example.farmmobileapp.feature.users.data.api

import com.example.farmmobileapp.feature.users.data.model.UserProfileResponse

interface UserProfileApi {
    suspend fun getUserProfile(): UserProfileResponse
}