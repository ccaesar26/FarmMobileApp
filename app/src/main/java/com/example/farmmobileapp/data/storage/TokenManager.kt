package com.example.farmmobileapp.data.storage

interface TokenManager {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}