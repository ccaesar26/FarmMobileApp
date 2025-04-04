package com.example.farmmobileapp.core.data.remote.api

import com.example.farmmobileapp.core.data.preferences.model.RetrieveResponse

interface ConfigApi {
    suspend fun getMapboxAccessToken(): RetrieveResponse
}